package ru.fizteh.fivt.students.sautin1.telnet.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sautin1 on 12/15/14.
 */
public class LoggingInvocationHandlerJSON implements InvocationHandler {
    private final Appendable writer;
    private final Object target;

    public LoggingInvocationHandlerJSON(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }

    private Object log(Method method, Object[] args) throws Throwable {
        Throwable thrownException = null;
        Object returnValue = null;

        JSONLogger logger = new JSONLogger();
        logger.logMethodCall(method, args, target);
        try {
            returnValue = simpleInvoke(method, args);
            if (method.getReturnType() != void.class) {
                logger.logReturnValue(returnValue);
            }
        } catch (Throwable e) {
            thrownException = e;
            logger.logThrown(thrownException);
        }

        writer.append(logger.toString());

        if (thrownException == null) {
            return returnValue;
        } else {
            throw thrownException;
        }
    }

    private Object simpleInvoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!Object.class.equals(method.getDeclaringClass())) {
            return log(method, args);
        }

        return simpleInvoke(method, args);
    }
}
