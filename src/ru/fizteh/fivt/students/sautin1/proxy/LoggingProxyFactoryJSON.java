package ru.fizteh.fivt.students.sautin1.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by sautin1 on 12/15/14.
 */
public class LoggingProxyFactoryJSON implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if ((writer == null) || (implementation == null) || (interfaceClass == null)) {
            throw new IllegalArgumentException("one of arguments is null");
        }
        if (!(interfaceClass.isInterface())) {
            throw new IllegalArgumentException("passed class is not an interface");
        }
        if (!(interfaceClass.isInstance(implementation))) {
            throw new IllegalArgumentException("passed class doesn't implement passed interface");
        }

        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[]{interfaceClass},
                new LoggingInvocationHandlerJSON(implementation, writer));
    }
}
