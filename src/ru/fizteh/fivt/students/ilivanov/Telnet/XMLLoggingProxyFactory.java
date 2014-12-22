package ru.fizteh.fivt.students.ilivanov.Telnet;

import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.LoggingProxyFactory;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.lang.reflect.Proxy;

public class XMLLoggingProxyFactory implements LoggingProxyFactory{

    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("wrap: null writer");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("wrap: null implementation");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("wrap: null interfaceClass");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("wrap: interfaceClass is not an interface");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("wrap: object doesn't implement specified interface");
        }
        try {
            XMLLoggingProxyInvocationHandler handler = new XMLLoggingProxyInvocationHandler(implementation, writer);
            return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{interfaceClass}, handler);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
