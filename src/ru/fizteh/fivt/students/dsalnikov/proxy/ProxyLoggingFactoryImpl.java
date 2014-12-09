package ru.fizteh.fivt.students.dsalnikov.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.dsalnikov.utils.CorrectnessCheck;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class ProxyLoggingFactoryImpl implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (!CorrectnessCheck.correctProxyArguments(writer, implementation, interfaceClass)) {
            throw new IllegalArgumentException("incorrect arguments supplied to proxy logging factory");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new ProxyInvocationHandlerImpl(writer, implementation));
    }
}
