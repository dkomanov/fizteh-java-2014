package ru.fizteh.fivt.students.irina_karatsapova.proxy.logs;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class MyLoggingProxyFactory implements LoggingProxyFactory {
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        checkCorrectInput(writer, implementation, interfaceClass);
        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new LoggingProxyInvocationHandler(writer, implementation));
    }



    private void checkCorrectInput(Writer writer, Object implementation, Class<?> interfaceClass) {
        try {
            Utils.checkNotNull(writer);
            Utils.checkNotNull(implementation);
            Utils.checkNotNull(interfaceClass);
            if (!interfaceClass.isInterface()) {
                throw new IllegalArgumentException("Third argument should be interface");
            }
            if (!interfaceClass.isInstance(implementation)) {
                throw new IllegalArgumentException("Secong argument should be implentation of the third");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("LoggingProxyFactory: wrap: " + e.getMessage());
        }
    }
}
