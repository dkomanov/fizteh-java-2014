package ru.fizteh.fivt.students.moskupols.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by moskupols on 24.12.14.
 */
public class LoggingInvocationHandler implements InvocationHandler {
    private final Writer writer;
    private final Lock writerLock;

    private final InvocationSerializer serializer;
    private final Object impl;
    private final Class<?> interfaceToLog;

    public LoggingInvocationHandler(
            Writer writer, InvocationSerializer serializer, Object impl, Class<?> interfaceToLog) {
        this.writer = writer;
        this.serializer = serializer;
        this.impl = impl;
        this.interfaceToLog = interfaceToLog;
        writerLock = new ReentrantLock();
    }

    protected boolean loggingWorth(Method m) {
        return m.getDeclaringClass().equals(interfaceToLog);
    }

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = null;
        Throwable thrown = null;
        try {
            returnValue = method.invoke(impl, args);
        } catch (InvocationTargetException e) {
            thrown = e.getCause();
        }

        try {
            if (loggingWorth(method)) {
                final String log = serializer.serialize(method, args, returnValue, thrown);

                writerLock.lock();
                try {
                    writer.write(log);
                } finally {
                    writerLock.unlock();
                }
            }
        } finally {
            if (thrown != null) {
                throw thrown;
            }
            return returnValue;
        }
    }
}
