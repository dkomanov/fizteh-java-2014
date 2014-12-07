package ru.fizteh.fivt.students.kolmakov_sergey.proxy.logging;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

class LoggingInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;

    public LoggingInvocationHandler(Writer writer, Object implementation) {
        if (writer == null || implementation == null){
            throw new IllegalArgumentException("Invocation handler constructor: null arguments");
        }
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try { // If it's method of Object, we just invoke it without logging.
            Object.class.getMethod(method.getName());
            return method.invoke(args);
        } catch (NoSuchMethodException e) {}

        XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        try {
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
            xmlWriter.writeAttribute("class", implementation.getClass().getName());
            xmlWriter.writeAttribute("name", method.getName());

            if (args == null || args.length == 0) {
                xmlWriter.writeEmptyElement("arguments");
            } else {
                xmlWriter.writeStartElement("arguments");
                for (Object currentArgument : args) {
                    xmlWriter.writeStartElement("argument");
                    if (currentArgument == null) {
                        xmlWriter.writeEmptyElement("null");
                        xmlWriter.writeEndElement();
                    } else if (currentArgument instanceof Iterable) {
                        recoursiveIterableWriting(xmlWriter, (Iterable) currentArgument, new IdentityHashMap<>());
                    } else {
                        xmlWriter.writeCharacters(currentArgument.toString());
                    }
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();

            // Invocation, logging of result.
            Object returnedValue;
            try {
                returnedValue = method.invoke(implementation, args);
                xmlWriter.writeStartElement("return");
                if (returnedValue != null) {
                    xmlWriter.writeCharacters(returnedValue.toString());
                } else {
                    xmlWriter.writeCharacters("null");
                }
                xmlWriter.writeEndElement();

                xmlWriter.writeEndElement();
                return returnedValue;
            } catch (InvocationTargetException e) { // Logging of exceptions.
                Throwable targetException = e.getTargetException();
                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(targetException.getClass().getName() + ": " + targetException.getMessage());
                xmlWriter.writeEndElement();

                xmlWriter.writeEndElement();
                throw targetException;
            }
        } catch (XMLStreamException e) {
            writer.write("error while logging method: " + e.getMessage());
            return null;
        } finally {
            xmlWriter.flush();
            xmlWriter.close();
        }
    }

    private void recoursiveIterableWriting(XMLStreamWriter xmlWriter, Iterable iterable,
                                           IdentityHashMap<Object, Object> antiCyclicMap) throws Throwable {
        antiCyclicMap.put(iterable, null);
        xmlWriter.writeStartElement("list");
        for (Object currentObject : iterable) {
            xmlWriter.writeStartElement("value");
            if (currentObject == null) {
                xmlWriter.writeEmptyElement("null");
                xmlWriter.writeEndElement();
            } else if (currentObject instanceof Iterable) {
                if (antiCyclicMap.containsKey(currentObject)){
                    xmlWriter.writeCharacters("cyclic");
                } else {
                    recoursiveIterableWriting(xmlWriter, (Iterable) currentObject, antiCyclicMap);
                }
            } else {
                xmlWriter.writeCharacters(currentObject.toString());
            }
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();
        antiCyclicMap.remove(iterable);
    }
}
