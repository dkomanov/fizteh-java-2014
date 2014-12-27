package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.io.Writer;
import java.lang.reflect.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

public class LoggingProxyFactoryImpl implements LoggingProxyFactory {
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
