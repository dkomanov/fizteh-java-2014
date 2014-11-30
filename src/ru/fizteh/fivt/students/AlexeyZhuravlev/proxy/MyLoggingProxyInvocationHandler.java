package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author AlexeyZhuravlev
 */
public class MyLoggingProxyInvocationHandler implements InvocationHandler {

    private Writer log;
    private Object target;

    public MyLoggingProxyInvocationHandler(Writer writer, Object implementation) {
        log = writer;
        target = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
