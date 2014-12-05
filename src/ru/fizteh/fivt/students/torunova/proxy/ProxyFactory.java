package ru.fizteh.fivt.students.torunova.proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;

/**
 * Created by nastya on 02.12.14.
 */
public class ProxyFactory implements LoggingProxyFactory {
    class Logger implements InvocationHandler {
        private Object implementation;
        private Document document;
        private Transformer transformer;
        private Writer writer;
        public Logger(Object newImplementation, Writer newWriter) {
            implementation = newImplementation;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                //ignored.
            }
            document = builder.newDocument();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                //ignored.
            }
            writer = newWriter;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Element invoke = document.createElement("invoke");
            invoke.setAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
            invoke.setAttribute("class", implementation.getClass().getName());
            invoke.setAttribute("name", method.getName());
            document.appendChild(invoke);
            Element arguments = document.createElement("arguments");
            invoke.appendChild(arguments);
            Element argument;
            for (Object obj: args) {
                argument = document.createElement("argument");
                if (obj instanceof Iterable) {
                    logIterable(argument, (Iterable) obj, new IdentityHashMap());
                } else {
                    argument.appendChild(document.createTextNode(obj.toString()));
                }
                arguments.appendChild(argument);
            }
            Element returnTag = document.createElement("return");
            Object result = null;
            try {
                result = method.invoke(implementation, args);
            } catch (InvocationTargetException e) {
                Element thrown = document.createElement("thrown");
                thrown.appendChild(document.createTextNode(e.getMessage()));
                invoke.appendChild(thrown);
                transformer.transform(new DOMSource(document), new StreamResult(writer));
                throw e.getTargetException();

            }
            returnTag.appendChild(document.createTextNode(result.toString()));
            invoke.appendChild(returnTag);
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return result;
        }
        private void logIterable(Element parent, Iterable iterable, IdentityHashMap elements) {
            Element value;
            Element list = document.createElement("list");
            parent.appendChild(list);
            for (Object obj: iterable) {
                value = document.createElement("value");
                if (elements.containsKey(obj)) {
                    value.appendChild(document.createTextNode("cyclic"));
                    return;
                } else if (obj instanceof Iterable) {
                    IdentityHashMap map = new IdentityHashMap(elements);
                    map.put(obj, null);
                    logIterable(value, (Iterable) obj, map);
                } else {
                    value.appendChild(document.createTextNode(obj.toString()));
                }
                list.appendChild(value);
            }

        }
    }
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] {interfaceClass}, new Logger(implementation, writer));
    }
}
