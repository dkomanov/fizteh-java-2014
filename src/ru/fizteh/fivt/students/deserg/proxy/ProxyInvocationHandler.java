package ru.fizteh.fivt.students.deserg.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Created by deserg on 10.12.14.
 */
public class ProxyInvocationHandler implements InvocationHandler {

    private Writer writer;
    private Object object;

    public ProxyInvocationHandler(Writer writer, Object object) {
        this.writer = writer;
        this.object = object;
    }

    private void xmlObject(Object object, XMLStreamWriter xmlWriter, IdentityHashMap<Object, Object> map) throws XMLStreamException {

        if (object == null) {
            xmlWriter.writeEmptyElement("null");
        } else {
            if (object instanceof Iterable) {

                if (map.containsKey(object)) {
                    xmlWriter.writeEmptyElement("cyclic");
                } else {
                    map.put(object, null);
                    xmlWriter.writeStartElement("list");
                    for (Object insideObject: (Iterable) object) {
                        xmlWriter.writeStartElement("value");
                        xmlObject(insideObject, xmlWriter, map);
                        xmlWriter.writeEndElement();
                    }

                    xmlWriter.writeEndElement();
                }
            } else {
                xmlWriter.writeEmptyElement(object.toString());
            }
        }

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{

        try {

            XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = xmlFactory.createXMLStreamWriter(writer);
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
            xmlWriter.writeAttribute("class", object.getClass().getName());
            xmlWriter.writeAttribute("name", method.getName());

            if (args == null || args.length == 0) {
                xmlWriter.writeEmptyElement("arguments");
            } else {
                xmlWriter.writeStartElement("arguments");

                for (Object object: args) {
                    xmlWriter.writeStartElement("argument");
                    xmlObject(object, xmlWriter, new IdentityHashMap<>());
                    xmlWriter.writeEndElement();
                }

                xmlWriter.writeEndElement();
            }

            try {

                Object result = method.invoke(object, args);
                if (method.getReturnType() != void.class) {
                    xmlWriter.writeStartElement("result");
                    xmlObject(result, xmlWriter, new IdentityHashMap<>());
                    xmlWriter.writeEndElement();
                }

                return result;

            } catch (InvocationTargetException ex) {

                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(ex.getTargetException().toString());
                xmlWriter.writeEndElement();
                throw ex.getTargetException();
            } finally {

                xmlWriter.writeEndDocument();
                xmlWriter.flush();
                xmlWriter.close();

            }


        } catch (XMLStreamException ex) {
            return null;
        }

    }

}
