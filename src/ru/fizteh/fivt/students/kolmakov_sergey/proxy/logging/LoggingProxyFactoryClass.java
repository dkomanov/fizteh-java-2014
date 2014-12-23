package ru.fizteh.fivt.students.kolmakov_sergey.proxy.logging;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class LoggingProxyFactoryClass implements LoggingProxyFactory {

    public LoggingProxyFactoryClass() {
        // Realization of interface must have public constructor without parameters.
    }

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null || implementation == null || interfaceClass == null || !interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Wrap method: incorrect arguments were found.");
        }
        Class<?>[] interfaceClassAsArray = new Class[]{interfaceClass};
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                interfaceClassAsArray, new LoggingInvocationHandler(writer, implementation));
    }
}
