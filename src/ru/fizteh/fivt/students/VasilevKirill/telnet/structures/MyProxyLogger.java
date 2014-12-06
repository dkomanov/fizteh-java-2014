package ru.fizteh.fivt.students.VasilevKirill.telnet.structures;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Kirill on 29.11.2014.
 */
public class MyProxyLogger implements InvocationHandler {
    private Object object;
    private Writer writer;

    public MyProxyLogger(Object object, Writer writer) {
        this.object = object;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object retValue = null;
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            if (xmlWriter == null) {
                return null;
            }
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp", ((Long) System.currentTimeMillis()).toString());
            xmlWriter.writeAttribute("class", object.getClass().toString());
            xmlWriter.writeAttribute("name", method.getName());
            putArguments(xmlWriter, args);
            Throwable throwable = null;
            try {
                retValue = method.invoke(object, args);
            } catch (InvocationTargetException e) {
                throwable = e.getTargetException();
            }
            if (throwable != null) {
                putThrowable(xmlWriter, throwable);
            } else {
                putReturnValue(xmlWriter, method, retValue);
            }
            xmlWriter.writeEndElement();
        } catch (Exception e) {
            int a = 1; //Just for checkstyle
        }
        return null;
    }

    public void putArguments(XMLStreamWriter xmlWriter, Object[] args) throws XMLStreamException {
        if (args == null) {
            xmlWriter.writeEmptyElement("arguments");
        } else {
            xmlWriter.writeStartElement("arguments");
            IdentityHashMap<Object, Object> map = new IdentityHashMap<>();
            List<Object> list = new ArrayList<>(Arrays.asList(args));
            recursivePrintArgs(xmlWriter, list, map, 0);
            xmlWriter.writeEndElement();
        }
    }

    public void putThrowable(XMLStreamWriter xmlWriter, Throwable e) throws XMLStreamException {
        xmlWriter.writeStartElement("thrown");
        xmlWriter.writeCharacters(e.toString());
        xmlWriter.writeEndElement();
    }

    public void putReturnValue(XMLStreamWriter xmlWriter, Method method, Object retValue) throws XMLStreamException {
        if (method.getReturnType() != void.class) {
            xmlWriter.writeStartElement("return");
            if (retValue == null) {
                xmlWriter.writeEmptyElement("null");
            } else {
                xmlWriter.writeCharacters(retValue.toString());
            }
            xmlWriter.writeEndElement();
        }
    }

    public void recursivePrintArgs(XMLStreamWriter xmlWriter, Iterable list, IdentityHashMap map, int argOrValue) {
        try {
            map.put(list, null);
            for (Object it : list) {
                if (argOrValue == 0) {
                    xmlWriter.writeStartElement("argument");
                } else {
                    xmlWriter.writeStartElement("value");
                }
                if (it instanceof Iterable) {
                    if (map.containsKey(it)) {
                        xmlWriter.writeCharacters("cyclic");
                    } else {
                        xmlWriter.writeStartElement("list");
                        recursivePrintArgs(xmlWriter, (Iterable) it, map, 1);
                        xmlWriter.writeEndElement();
                    }
                } else {
                    xmlWriter.writeCharacters(it.toString());
                }
                xmlWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            int a = 1;
        }
    }
}
