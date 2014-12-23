package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class MyLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class[] {interfaceClass},
                new MyLoggingProxyInvocationHandler(writer, implementation));
    }
}
