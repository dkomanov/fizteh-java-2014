package ru.fizteh.fivt.students.Bulat_Galiev.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

public class Logger implements InvocationHandler {
    private Writer xmlwriter;
    private Object object;

    public Logger(final Object implementation, final Writer writer) {
        object = implementation;
        this.xmlwriter = writer;
    }

    @Override
    public final Object invoke(final Object proxy, final Method method,
            final Object[] args) throws Throwable {
        Object result = null;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter streamWriter = factory.createXMLStreamWriter(xmlwriter);
        try {
            streamWriter.writeStartDocument();
            streamWriter.writeStartElement("invoke");
            streamWriter.writeAttribute("timestamp",
                    String.valueOf(System.currentTimeMillis()));
            streamWriter.writeAttribute("class", object.getClass().getName());
            streamWriter.writeAttribute("name", method.getName());
            streamWriter.writeStartElement("arguments");
            if (args != null) {
                for (Object arg : args) {
                    streamWriter.writeStartElement("argument");
                    if (arg == null) {
                        streamWriter.writeEmptyElement("null");
                    } else if (arg instanceof Iterable) {
                        writeIterable((Iterable) arg, new IdentityHashMap<>(),
                                streamWriter);
                    } else {
                        streamWriter.writeCharacters(arg.toString());
                    }
                    streamWriter.writeEndElement();
                }
                streamWriter.writeEndElement();
            } else {
                streamWriter.writeStartElement("argument");
                streamWriter.writeEmptyElement("null");
                streamWriter.writeEndElement();
                streamWriter.writeEndElement();
            }
            Throwable exceptionResult = null;

            try {
                result = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                exceptionResult = e.getTargetException();
            }

            try {
                if (exceptionResult != null) {
                    streamWriter.writeStartElement("thrown");
                    streamWriter.writeCharacters(exceptionResult.getClass()
                            .getName() + ": " + exceptionResult.getMessage());
                    throw exceptionResult;
                } else {
                    if (method.getReturnType() != void.class) {
                        streamWriter.writeStartElement("return");
                        if (result instanceof Iterable) {
                            writeIterable((Iterable) result,
                                    new IdentityHashMap<>(), streamWriter);
                        } else {
                            if (result == null) {
                                streamWriter.writeEmptyElement("null");
                            } else {
                                streamWriter.writeCharacters(result.toString());
                            }
                        }
                        streamWriter.writeEndElement();
                    }
                    return result;
                }
            } finally {
                streamWriter.writeEndElement();
                streamWriter.writeEndDocument();
                streamWriter.flush();
            }

        } catch (XMLStreamException e) {
            // Disable exception processing.
        }

        return null;
    }

    private void writeIterable(final Iterable iterable,
            final IdentityHashMap<Object, Object> map,
            final XMLStreamWriter streamWriter) throws XMLStreamException {
        map.put(iterable, null);
        streamWriter.writeStartElement("list");
        for (Object element : iterable) {
            if (element instanceof Iterable) {
                if (map.containsKey(element)) {
                    streamWriter.writeStartElement("value");
                    streamWriter.writeCharacters("cyclic");
                    streamWriter.writeEndElement();
                } else {
                    writeIterable(iterable, map, streamWriter);
                }
            } else {
                streamWriter.writeStartElement("value");
                streamWriter.writeCharacters(element.toString());
                streamWriter.writeEndElement();
            }
        }
        streamWriter.writeEndElement();
    }
}
