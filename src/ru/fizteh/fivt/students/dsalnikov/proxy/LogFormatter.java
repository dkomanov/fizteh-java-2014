package ru.fizteh.fivt.students.dsalnikov.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;


public class LogFormatter implements Closeable {
    private final IdentityHashMap<Object, Boolean> cycleLinkCheck = new IdentityHashMap<>();
    private StringWriter stringWriter = new StringWriter();
    private XMLStreamWriter streamWriter;

    public LogFormatter() throws IOException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try {
            streamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            streamWriter.writeStartElement("invoke");
        } catch (XMLStreamException exc) {
            throw new IOException("LogFormatter creation fail " + exc.getMessage());
        }
    }

    public void writeTimeStamp() throws IOException {
        try {
            streamWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        } catch (XMLStreamException e) {
            throw new IOException("writing timestamp error. Probably something wrong with System.currentTimeMillis()");
        }
    }

    public void writeMethod(Method method) throws IOException {
        try {
            streamWriter.writeAttribute("name", method.getName());
        } catch (XMLStreamException e) {
            throw new IOException("writing method failed: Could it be something wrong with method access?" + e.getMessage());
        }
    }

    public void writeClass(Class<?> clazz) throws IOException {
        try {
            streamWriter.writeAttribute("class", clazz.getName());
        } catch (XMLStreamException e) {
            throw new IOException("writing class failed. Could it be something wrong with class access?");
        }
    }

    public void writeArgs(Object[] args) throws IOException {
        try {
            streamWriter.writeStartElement("arguments");
            if (args != null) {
                recursiveWrite(Arrays.asList(args), streamWriter, false, false);
            }
            streamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("arguments writing error.");
        }

    }

    public void writeReturnValue(Object result) throws IOException {
        try {
            streamWriter.writeStartElement("return");
            if (result != null) {
                streamWriter.writeCharacters(result.toString());
            } else {
                streamWriter.writeStartElement("null");
                streamWriter.writeEndElement();
            }
            streamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("writing return value failed");
        }
    }

    public void writeExceptionThrown(Throwable throwable) throws IOException {
        try {
            streamWriter.writeStartElement("thrown");
            streamWriter.writeCharacters(throwable.toString());
            streamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new IOException("Exception thrown clause failed.");
        }
    }

    private void recursiveWrite(Iterable collection, XMLStreamWriter streamWriter, boolean inList, boolean cycled) throws XMLStreamException {
        boolean isContainer;
        boolean isEmpty;
        for (Object object : collection) {
            if (object == null) {
                if (inList) {
                    streamWriter.writeStartElement("value");
                } else {
                    streamWriter.writeStartElement("argument");
                }
                streamWriter.writeStartElement("null");
                streamWriter.writeEndElement();
                streamWriter.writeEndElement();
                continue;
            }

            if (object.getClass().isArray()) {
                if (inList) {
                    streamWriter.writeStartElement("value");
                } else {
                    streamWriter.writeStartElement("argument");
                }
                streamWriter.writeCharacters(object.toString());
                streamWriter.writeEndElement();
                continue;
            }

            isContainer = false;
            isEmpty = false;

            if (object instanceof Iterable) {
                isContainer = true;
                isEmpty = !((Iterable) object).iterator().hasNext();
            }

            if (cycleLinkCheck.containsKey(object) && isContainer && !isEmpty) {
                cycled = true;
            }
            cycleLinkCheck.put(object, true);
            if (isContainer) {
                if (!cycled && !inList) {
                    streamWriter.writeStartDocument("argument");
                } else {
                    streamWriter.writeStartElement("value");
                }
                streamWriter.writeStartElement("list");
                inList = true;
                if (!cycled) {
                    recursiveWrite((Iterable) object, streamWriter, inList, cycled);
                    streamWriter.writeEndElement();
                } else {
                    for (Object objectInsideIterable : (Iterable) object) {
                        if (objectInsideIterable == null) {
                            streamWriter.writeStartElement("value");
                            streamWriter.writeStartElement("null");
                            streamWriter.writeEndElement();
                            streamWriter.writeEndElement();
                            continue;
                        }
                        if (objectInsideIterable instanceof Iterable) {
                            streamWriter.writeStartElement("value");
                            streamWriter.writeCharacters("cyclic");
                            streamWriter.writeEndElement();
                        }
                        streamWriter.writeStartElement("value");
                        streamWriter.writeCharacters(objectInsideIterable.toString());
                        streamWriter.writeEndElement();
                    }
                    streamWriter.writeEndElement();
                    cycled = false;
                }
                streamWriter.writeEndElement();
                inList = false;
                continue;
            }
            if (inList) {
                streamWriter.writeStartElement("value");
            } else {
                streamWriter.writeStartElement("argument");
            }
            streamWriter.writeCharacters(object.toString());
            streamWriter.writeEndElement();
        }
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }


    @Override
    public void close() throws IOException {
        try {
            streamWriter.writeEndElement();
            streamWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("closing problem.");
        }
    }
}
