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
    private final IdentityHashMap<Object, Boolean> forCycleLinkSearch = new IdentityHashMap<>();
    private StringWriter stringWriter = new StringWriter();
    private XMLStreamWriter streamWriter = null;

    public LogFormatter() throws IOException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try {
            streamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            streamWriter.writeStartElement("invoke");
        } catch (XMLStreamException exc) {
            throw new IOException("xml formatter creation error: " + exc.getMessage());
        }
    }

    public void writeTimeStamp() throws IOException {
        try {
            streamWriter.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        } catch (XMLStreamException exc) {
            throw new IOException("xml write timestamp error: " + exc.getMessage());
        }
    }

    public void writeClass(Class<?> clazz) throws IOException {
        try {
            streamWriter.writeAttribute("class", clazz.getName());
        } catch (XMLStreamException exc) {
            throw new IOException("xml write class error: " + exc.getMessage());
        }
    }

    public void writeMethod(Method method) throws IOException {
        try {
            streamWriter.writeAttribute("name", method.getName());
        } catch (XMLStreamException exc) {
            throw new IOException("xml write method name error: " + exc.getMessage());
        }
    }

    public void writeArguments(Object[] args) throws IOException {
        try {
            streamWriter.writeStartElement("arguments");
            if (args != null) {
                recursivePart(Arrays.asList(args), streamWriter, false, false);
            }
            streamWriter.writeEndElement();
        } catch (XMLStreamException exc) {
            throw new IOException("xml writing arguments error: " + exc.getMessage());

        }
        forCycleLinkSearch.clear();
    }

    private void recursivePart(Iterable collection, XMLStreamWriter xmlStreamWriter, boolean inList, boolean inCycle)
            throws XMLStreamException {
        boolean isContainer;
        boolean isEmpty;
        for (Object object : collection) {
            if (object == null) {
                if (inList) {
                    xmlStreamWriter.writeStartElement("value");
                } else {
                    xmlStreamWriter.writeStartElement("argument");
                }
                xmlStreamWriter.writeStartElement("null");
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndElement();
                continue;
            }

            if (object.getClass().isArray()) {
                if (inList) {
                    xmlStreamWriter.writeStartElement("value");
                } else {
                    xmlStreamWriter.writeStartElement("argument");
                }
                xmlStreamWriter.writeCharacters(object.toString());
                xmlStreamWriter.writeEndElement();
                continue;
            }

            isContainer = false;
            isEmpty = false;

            if (object instanceof Iterable) {
                isContainer = true;
                isEmpty = !((Iterable) object).iterator().hasNext();
            }

            if (forCycleLinkSearch.containsKey(object) && isContainer && !isEmpty) {
                inCycle = true;
            }
            forCycleLinkSearch.put(object, true);
            if (isContainer) {
                if (!inCycle && !inList) {
                    xmlStreamWriter.writeStartElement("argument");
                } else {
                    xmlStreamWriter.writeStartElement("value");
                }
                xmlStreamWriter.writeStartElement("list");
                inList = true;
                if (!inCycle) {
                    recursivePart((Iterable) object, xmlStreamWriter, inList, inCycle);
                    xmlStreamWriter.writeEndElement();
                } else {
                    for (Object objectInsideIterable : (Iterable) object) {
                        if (objectInsideIterable == null) {
                            xmlStreamWriter.writeStartElement("value");
                            xmlStreamWriter.writeStartElement("null");
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeEndElement();
                            continue;
                        }
                        if (objectInsideIterable instanceof Iterable) {
                            xmlStreamWriter.writeStartElement("value");
                            xmlStreamWriter.writeCharacters("cyclic");
                            xmlStreamWriter.writeEndElement();
                            continue;
                        }
                        xmlStreamWriter.writeStartElement("value");
                        xmlStreamWriter.writeCharacters(objectInsideIterable.toString());
                        xmlStreamWriter.writeEndElement();
                    }
                    xmlStreamWriter.writeEndElement();
                    inCycle = false;
                }
                xmlStreamWriter.writeEndElement();
                inList = false;
                continue;
            }
            if (inList) {
                xmlStreamWriter.writeStartElement("value");
            } else {
                xmlStreamWriter.writeStartElement("argument");
            }
            xmlStreamWriter.writeCharacters(object.toString());
            xmlStreamWriter.writeEndElement();
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
        } catch (XMLStreamException exc) {
            throw new IOException("xml write return value error: " + exc.getMessage());
        }
    }

    public void writeThrown(Throwable throwable) throws IOException {
        try {
            streamWriter.writeStartElement("thrown");
            streamWriter.writeCharacters(throwable.toString());
            streamWriter.writeEndElement();
        } catch (XMLStreamException exc) {
            throw new IOException("xml write thrown error: " + exc.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        try {
            streamWriter.writeEndElement();
            streamWriter.flush();
        } catch (XMLStreamException exc) {
            throw new IOException("xml stream writer close error: " + exc.getMessage());
        }
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }
}
