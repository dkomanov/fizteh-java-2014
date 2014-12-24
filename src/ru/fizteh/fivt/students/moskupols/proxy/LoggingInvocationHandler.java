package ru.fizteh.fivt.students.moskupols.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by moskupols on 24.12.14.
 */
public class LoggingInvocationHandler implements InvocationHandler {
    private final Writer writer;
    private final InvocationSerializer serializer;
    private final Object impl;

    public LoggingInvocationHandler(Writer writer, InvocationSerializer serializer, Object impl) {
        this.writer = writer;
        this.serializer = serializer;
        this.impl = impl;
    }

    protected boolean loggingWorth(Method m) {
        // TODO: ignore calls to methods inherited from Object
        return true;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = method.invoke(impl, args);
        if (loggingWorth(method)) { // TODO: ignore throws here
            final String log = serializer.serialize(method, args, returnValue);
            // TODO: thread safety
            writer.write(log);
        }
        return returnValue;
    }
}
