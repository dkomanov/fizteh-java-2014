package ru.fizteh.fivt.students.gudkov394.Proxy;


import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by kagudkov on 30.11.14.
 */
public class MyWriteProxyHandler implements InvocationHandler {
    private Writer writer;
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
        if(!exception) {
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
}
