package ru.fizteh.fivt.students.irina_karatsapova.proxy.logs;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoggingProxyInvocationHandler implements InvocationHandler {
    private Object implementation;
    private Writer writer;

    LoggingProxyInvocationHandler(Writer writer, Object implementation) {
        this.implementation = implementation;
        this.writer = writer;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Method objectMethod : Object.class.getMethods()) {
            if (objectMethod.equals(method)) {
                return method.invoke(implementation, args);
            }
        }

        JsonLogCreator logCreator = new JsonLogCreator();
        logCreator.writeTimestamp();
        logCreator.writeClass(implementation.getClass());
        logCreator.writeMethod(method);
        logCreator.writeArguments(args);
        Object result;
        try {
            result = method.invoke(implementation, args);
            if (method.getReturnType() != void.class) {
                logCreator.writeReturnValue(result);
            }
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            logCreator.writeThrown(targetException);
            throw targetException;
        } finally {
            writer.write(logCreator.getLog());
        }
        return result;
    }
}
