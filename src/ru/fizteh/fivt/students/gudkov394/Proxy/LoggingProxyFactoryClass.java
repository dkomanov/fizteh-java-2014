package ru.fizteh.fivt.students.gudkov394.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by kagudkov on 30.11.14.
 */
public class LoggingProxyFactoryClass implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[] {interfaceClass},
                new MyWriteProxyHandler(writer, implementation));
    }
}
