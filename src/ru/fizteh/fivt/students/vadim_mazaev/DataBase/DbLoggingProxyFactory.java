package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class DbLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation,
            Class<?> interfaceClass) {
        if (writer == null || implementation == null || interfaceClass == null
                || !interfaceClass.isInterface()) {
            throw new IllegalArgumentException();
        }
        return Proxy.newProxyInstance(implementation.getClass()
                .getClassLoader(), new Class<?>[] {interfaceClass},
                new DbLogger(writer, implementation));
    }
}

class DbLogger implements InvocationHandler {
    private Writer writer;
    private Object object;
    private String objectName;

    public DbLogger(Writer writer, Object object) {
        this.writer = writer;
        this.object = object;
        objectName = object.getClass().getName();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (isMethodOfObject(method)) {
            return method.invoke(args);
        }
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = output.createXMLStreamWriter(writer);
        try {
            xmlWriter.writeStartElement("invoke");
            xmlWriter.writeAttribute("timestamp",
                    String.valueOf(System.currentTimeMillis()));
            xmlWriter.writeAttribute("class", objectName);
            xmlWriter.writeAttribute("name", method.getName());

            if (args == null || args.length == 0) {
                xmlWriter.writeEmptyElement("arguments");
            } else {
                xmlWriter.writeStartElement("arguments");
                for (Object arg : args) {
                    xmlWriter.writeStartElement("argument");
                    if (arg == null) {
                        xmlWriter.writeEmptyElement("null");
                        xmlWriter.writeEndElement();
                    } else if (arg instanceof Iterable) {
                        writeIterable(xmlWriter, (Iterable<?>) arg);
                    } else {
                        xmlWriter.writeCharacters(arg.toString());
                    }
                    xmlWriter.writeEndElement();
                }
            }
            xmlWriter.writeEndElement();

            try {
                Object result = method.invoke(object, args);
                if (method.getReturnType() != void.class) {
                    xmlWriter.writeStartElement("return");
                    xmlWriter.writeCharacters(result.toString());
                    xmlWriter.writeEndElement();
                }

                xmlWriter.writeEndElement();
                return result;
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                xmlWriter.writeStartElement("thrown");
                xmlWriter.writeCharacters(target.getClass().getName() + ": "
                        + target.getMessage());
                xmlWriter.writeEndElement();

                xmlWriter.writeEndElement();
                throw target;
            }
        } catch (XMLStreamException e) {
            writer.write("Unable to log method invocation: " + e.getMessage());
            try {
                return method.invoke(object, args);
            } catch (InvocationTargetException te) {
                throw te.getTargetException();
            }
        } finally {
            xmlWriter.flush();
            xmlWriter.close();
        }
    }

    private boolean isMethodOfObject(Method method) {
        try {
            Object.class.getMethod(method.getName());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void writeIterable(XMLStreamWriter xmlWriter, Iterable<?> iterable)
            throws Throwable {
        xmlWriter.writeStartElement("list");
        for (Object value : iterable) {
            xmlWriter.writeStartElement("value");
            if (value == null) {
                xmlWriter.writeEmptyElement("null");
                xmlWriter.writeEndElement();
            } else if (value instanceof Iterable) {
                writeIterable(xmlWriter, (Iterable<?>) value);
            } else {
                xmlWriter.writeCharacters(value.toString());
            }
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();
    }
}
