package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by moskupols on 24.12.14.
 */
public class LoggingProxyFactoryImpl implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[] {interfaceClass },
                new LoggingInvocationHandler(
                        writer, new JsonInvocationSerializer(), implementation, interfaceClass));
    }
}
