package ru.fizteh.fivt.students.gudkov394.Proxy;


import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Created by kagudkov on 30.11.14.
 */
public class MyWriteProxyHandler implements InvocationHandler {

    private Writer writer;
    private Object implementation;

    MyWriteProxyHandler(Writer wr, Object impl) {
        writer = wr;
        implementation = impl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<invoke timestamp=\"" + System.currentTimeMillis() + "\" class=\""
                + implementation.getClass().getCanonicalName() + "\" name=\"" + method.getName() + "\">");
        if (args != null && args.length > 0) {
            stringBuilder.append((new ParserXML()).parse(args));
        } else {
            stringBuilder.append("<arguments/>");
        }
        try {
            Object result;
            if (args != null) {
                result = method.invoke(implementation, args);
            } else {
                result = method.invoke(implementation);
            }
            if (method.getReturnType() != void.class) {
                String res;
                if (result == null || result instanceof Iterable) {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.newDocument();
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    StringWriter stringWriter = new StringWriter();
                    transformer.transform(new DOMSource((new ParserXML()).parseObject(result,
                                    new IdentityHashMap<>(), document)),
                            new StreamResult(stringWriter));
                    res = stringWriter.toString();
                } else {
                    res = result.toString();
                }
                stringBuilder.append("<return>" + res + "</return>");
            }
            return result;
        } catch (InvocationTargetException e) {
            stringBuilder.append("<thrown>" + e.getTargetException() + "</thrown>");
            throw e;
        } finally {
            stringBuilder.append("</invoke>");
            try {
                writer.write(stringBuilder.toString());
            } catch (IOException e) {
                System.out.println("Can't write log");
            }
            writer.flush();
        }
    }
}
  /*  private Writer writer;
    private Object object;

    public MyWriteProxyHandler(Writer writerTmp, Object implementation) {
        writer = writerTmp;
        object = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = factory.createXMLStreamWriter(writer);
        if (xmlStreamWriter == null) {
            return null;
        }
        xmlStreamWriter.writeStartElement("invoke");
        xmlStreamWriter.writeAttribute("timestamp", ((Long) System.currentTimeMillis()).toString());
        xmlStreamWriter.writeAttribute("class", object.getClass().getSimpleName());
        xmlStreamWriter.writeAttribute("name", method.getName());
        writeArguments(xmlStreamWriter, args);
        Object result = null;
        boolean exception = false;
        try {
            result = method.invoke(object, args);
        } catch (Throwable e) {
            exception = true;
            xmlStreamWriter.writeStartElement("thrown");
            xmlStreamWriter.writeCharacters(e.toString());
            xmlStreamWriter.writeEndElement();
        }
        if (!exception) {

            if (method.getReturnType() != void.class) {
                xmlStreamWriter.writeStartElement("return");
                if (result == null) {
                    xmlStreamWriter.writeEmptyElement("null");
                } else {
                    xmlStreamWriter.writeCharacters(result.toString());
                }
                xmlStreamWriter.writeEndElement();
            }
        }
        xmlStreamWriter.writeEndElement();
        return null;
    }

    private void writeArguments(XMLStreamWriter xmlStreamWriter, Object[] args) throws XMLStreamException {
        if (args == null) {
            xmlStreamWriter.writeEmptyElement("arguments");
        } else {
            xmlStreamWriter.writeStartElement("arguments");
            IdentityHashMap<Object, Object> used = new IdentityHashMap<>();
            List<Object> references = new ArrayList<>();
            dfs(xmlStreamWriter, references, used, true);
        }
    }

    private void dfs(XMLStreamWriter xmlStreamWriter, List<Object> references, IdentityHashMap<Object, Object> used,
                     boolean argument) throws XMLStreamException {
        used.put(references, null);
        for (Object ref : references) {
            if (argument) {
                xmlStreamWriter.writeStartElement("argument");
            } else {
                xmlStreamWriter.writeStartElement("value");
            }
            if (ref instanceof Iterable) {
                if (used.containsKey(ref)) {
                    xmlStreamWriter.writeStartElement("cyclic");
                } else {
                    xmlStreamWriter.writeStartElement("list");
                    dfs(xmlStreamWriter, (List<Object>) ref, used, false);
                }
            } else {
                xmlStreamWriter.writeCharacters(ref.toString());
            }
            xmlStreamWriter.writeEndElement();
        }
    }
    */
