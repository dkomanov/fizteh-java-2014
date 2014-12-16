package ru.fizteh.fivt.students.ryad0m.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class LoggingFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new LoggingInvocationHandler(writer, implementation));
    }
}
