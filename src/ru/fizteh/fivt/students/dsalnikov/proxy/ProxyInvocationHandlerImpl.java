package ru.fizteh.fivt.students.dsalnikov.proxy;

import ru.fizteh.fivt.students.dsalnikov.utils.CorrectnessCheck;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProxyInvocationHandlerImpl implements InvocationHandler {
    private final Writer writer;
    private final Object implementation;
    private final Lock lock = new ReentrantLock(true);

    public ProxyInvocationHandlerImpl(Writer writer, Object object) {
        this.writer = writer;
        this.implementation = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!CorrectnessCheck.methodIsCorrectForProxy(method)) {
            return method.invoke(implementation, args);
        }
        Object result = null;
        lock.lock();
        try {
            LogFormatter formatter = new LogFormatter();
            formatter.writeTimeStamp();
            formatter.writeClass(implementation.getClass());
            formatter.writeMethod(method);
            formatter.writeArguments(args);
            try {
                result = method.invoke(implementation, args);
                if (!method.getReturnType().equals(void.class)) {
                    formatter.writeReturnValue(result);
                }
            } catch (InvocationTargetException ite) {
                Throwable exc = ite.getTargetException();
                formatter.writeThrown(exc);
                throw exc;
            } catch (Exception e) {
                //do nothing
            } finally {
                try {
                    if (method.getDeclaringClass() != Object.class) {
                        formatter.close();
                        writer.write(formatter.toString() + "\n");
                    }
                } catch (IOException ignoredException) {
                    //do nothing
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

}
