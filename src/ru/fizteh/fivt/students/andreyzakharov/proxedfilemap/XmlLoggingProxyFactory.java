package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class XmlLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new XmlLoggingProxyInvocationHandler(writer, implementation));
    }
}
