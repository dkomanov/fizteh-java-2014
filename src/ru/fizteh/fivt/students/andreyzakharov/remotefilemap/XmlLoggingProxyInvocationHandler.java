package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class XmlLoggingProxyInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object object;

    public XmlLoggingProxyInvocationHandler(Writer writer, Object object) {
        this.writer = writer;
        this.object = object;
    }

    private void serializeObject(Object object, XMLStreamWriter xsw, IdentityHashMap<Object, Object> set)
            throws XMLStreamException {
        if (object == null) {
            xsw.writeEmptyElement("null");
        } else {
            if (object instanceof Iterable) {
                if (set.containsKey(object)) {
                    xsw.writeEmptyElement("cyclic");
                } else {
                    set.put(object, null);
                    xsw.writeStartElement("list");
                    for (Object element : (Iterable) object) {
                        xsw.writeStartElement("value");
                        serializeObject(element, xsw, set);
                        xsw.writeEndElement();
                    }
                    xsw.writeEndElement();
                }
            } else {
                xsw.writeCharacters(object.toString());
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            XMLOutputFactory xml = XMLOutputFactory.newInstance();
            XMLStreamWriter xsw = xml.createXMLStreamWriter(writer);
            xsw.writeStartElement("invoke");
            xsw.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
            xsw.writeAttribute("class", object.getClass().getName());
            xsw.writeAttribute("name", method.getName());
            if (args == null || args.length == 0) {
                xsw.writeEmptyElement("arguments");
            } else {
                xsw.writeStartElement("arguments");
                for (Object arg : args) {
                    xsw.writeStartElement("argument");
                    serializeObject(arg, xsw, new IdentityHashMap<>());
                    xsw.writeEndElement();
                }
                xsw.writeEndElement();
            }

            try {
                Object result = method.invoke(object, args);
                if (method.getReturnType() != void.class) {
                    xsw.writeStartElement("return");
                    serializeObject(result, xsw, new IdentityHashMap<>());
                    xsw.writeEndElement();
                }
                return result;
            } catch (InvocationTargetException e) {
                xsw.writeStartElement("thrown");
                xsw.writeCharacters(e.getTargetException().toString());
                xsw.writeEndElement();
                throw e.getTargetException();
            } finally {
                xsw.writeEndDocument();
                xsw.writeCharacters("\n");
                xsw.flush();
                xsw.close();
            }
        } catch (XMLStreamException e) {
            return null;
        }
    }
}
