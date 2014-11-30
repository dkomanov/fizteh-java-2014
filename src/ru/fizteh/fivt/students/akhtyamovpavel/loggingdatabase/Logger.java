package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.io.File;

/**
 * Created by akhtyamovpavel on 30.11.14.
 */
public class Logger implements InvocationHandler {
    Writer writer;
    Object object;
    File file;
    public static final String LOG = "log.txt";
    public Logger(Object implementation, Writer writer) {
        object = implementation;
        this.writer = writer;
        file = new File(LOG);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = factory.createXMLStreamWriter(writer);
            streamWriter.writeStartDocument();
            streamWriter.writeStartElement("invoke");
            streamWriter.writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
            streamWriter.writeAttribute("class", object.getClass().getName());
            streamWriter.writeAttribute("method", method.getName());
            streamWriter.writeStartElement("arguments");
            if (args != null) {
                for (int i = 0; i < args.length; ++i) {
                    streamWriter.writeStartElement("argument");
                    if (args[i] instanceof Iterable) {
                        writeLists((Iterable) args[i], new IdentityHashMap<>(), streamWriter);
                    } else {
                        streamWriter.writeCharacters(args[i].toString());
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
                    streamWriter.writeCharacters(exceptionResult.toString());
                    throw exceptionResult;
                } else {
                    printReturnable(streamWriter, method, result);
                    return result;
                }
            } finally {
                streamWriter.writeEndElement();
                printState(streamWriter);
            }

        } catch (XMLStreamException e) {
            //skip
        }


        return null;
    }

    private void printReturnable(XMLStreamWriter streamWriter, Method method, Object result) throws XMLStreamException {
        if (method.getReturnType() != void.class) {
            streamWriter.writeStartElement("return");
            if (result instanceof Iterable) {
                writeLists((Iterable) result, new IdentityHashMap<>(), streamWriter);
            } else {
                if (result == null) {
                    streamWriter.writeEmptyElement("null");
                } else {
                    streamWriter.writeCharacters(result.toString());
                }
            }
            streamWriter.writeEndElement();
        }
    }

    private void writeLists(Iterable iterable,
                            IdentityHashMap<Object, Object> map,
                            XMLStreamWriter streamWriter) throws XMLStreamException {
        map.put(iterable, null);
        streamWriter.writeStartElement("list");
        for (Object element: iterable) {
            if (element instanceof Iterable) {
                if (map.containsKey(element)) {
                    streamWriter.writeStartElement("value");
                    streamWriter.writeCharacters("cyclic");
                    streamWriter.writeEndElement();
                } else {
                    writeLists(iterable, map, streamWriter);
                }
            } else {
                streamWriter.writeStartElement("value");
                streamWriter.writeCharacters(element.toString());
                streamWriter.writeEndElement();
            }
        }

        streamWriter.writeEndElement();

    }

    private void printState(XMLStreamWriter streamWriter) {
        try {
            synchronized (writer) {
                streamWriter.writeEndDocument();
                streamWriter.flush();
            }
        } catch (XMLStreamException e) {
            //skip
        }
    }
}
