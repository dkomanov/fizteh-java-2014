package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * @author AlexeyZhuravlev
 */
public class MyLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                                      new Class[] {interfaceClass},
                                      new MyLoggingProxyInvocationHandler(writer, implementation));
    }
}
