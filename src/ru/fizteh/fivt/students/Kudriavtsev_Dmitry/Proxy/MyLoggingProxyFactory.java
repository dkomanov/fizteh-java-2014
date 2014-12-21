package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;

/**
 * Created by ВАНЯ on 20.12.2014.
 */
public class MyLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        Class<?>[] interfaces = new Class<?>[1];
        interfaces[0] = interfaceClass;
        return java.lang.reflect.Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                interfaces, new MyLoggingProxy(implementation, writer));
    }
}
