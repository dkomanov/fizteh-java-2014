package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONComplexObject;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONField;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONMaker;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LoggingProxyFactoryImpl implements LoggingProxyFactory {
    private volatile boolean loggingEnabled = true;

    private static LoggingReport constructReport(long timestamp,
                                                 String invokeeClass,
                                                 String invokeeMethod,
                                                 Object[] arguments,
                                                 Throwable thrown,
                                                 Object returnedValue,
                                                 boolean isVoid) {
        if (thrown != null) {
            return new LoggingReportWithThrown(timestamp, invokeeClass, invokeeMethod, arguments, thrown);
        }
        if (isVoid) {
            return new LoggingReport(timestamp, invokeeClass, invokeeMethod, arguments);
        } else {
            return new LoggingReportWithReturnValue(
                    timestamp, invokeeClass, invokeeMethod, arguments, returnedValue);
        }
    }

    boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class<?>[] {interfaceClass},
                new Handler(writer, implementation));
    }

    @JSONComplexObject
    private static class LoggingReport {
        @JSONField
        long timestamp;

        @JSONField(name = "class")
        String invokeeClass;

        @JSONField(name = "method")
        String invokeeMethod;

        @JSONField
        Object[] arguments;

        public LoggingReport() {

        }

        public LoggingReport(long timestamp, String invokeeClass, String invokeeMethod, Object[] arguments) {
            this.timestamp = timestamp;
            this.invokeeClass = invokeeClass;
            this.invokeeMethod = invokeeMethod;
            this.arguments = arguments;
        }
    }

    @JSONComplexObject
    private static class LoggingReportWithReturnValue extends LoggingReport {
        @JSONField
        Object returnValue;

        public LoggingReportWithReturnValue() {

        }

        public LoggingReportWithReturnValue(long timestamp,
                                            String invokeeClass,
                                            String invokeeMethod,
                                            Object[] arguments,
                                            Object returnValue) {
            super(timestamp, invokeeClass, invokeeMethod, arguments);
            this.returnValue = returnValue;
        }
    }

    @JSONComplexObject
    private static class LoggingReportWithThrown extends LoggingReport {
        @JSONField
        Throwable thrown;

        public LoggingReportWithThrown() {

        }

        public LoggingReportWithThrown(long timestamp,
                                       String invokeeClass,
                                       String invokeeMethod,
                                       Object[] arguments,
                                       Throwable thrown) {
            super(timestamp, invokeeClass, invokeeMethod, arguments);
            this.thrown = thrown;
        }
    }

    private class Handler implements InvocationHandler {
        private final Writer writer;
        private final Object wrappedObject;

        public Handler(Writer writer, Object wrappedObject) {
            this.writer = writer;
            this.wrappedObject = wrappedObject;
        }

        @Override
        public String toString() {
            return wrappedObject.toString();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isObjectMethod;

            try {
                Object.class.getMethod(method.getName(), method.getParameterTypes());
                isObjectMethod = true;
            } catch (NoSuchMethodException exc) {
                isObjectMethod = false;
            }

            // We do not proxy methods of Object class. We call it on us.
            if (isObjectMethod) {
                try {
                    return method.invoke(this, args);
                } catch (InvocationTargetException exc) {
                    throw exc.getTargetException();
                }
            }

            long timestamp = System.currentTimeMillis();
            Object returnValue = null;
            Throwable thrown = null;

            // We must know it before invocation. Simple reason: suppose the invoked method turns off logging.
            boolean doWriteLog = isLoggingEnabled();

            try {
                boolean accessible = method.isAccessible();
                method.setAccessible(true);
                returnValue = method.invoke(wrappedObject, args);
                method.setAccessible(accessible);
            } catch (InvocationTargetException exc) {
                thrown = exc.getTargetException();
            } catch (IllegalAccessException | IllegalArgumentException exc) {
                Log.log(LoggingProxyFactory.class, exc, "Error on proxy invocation");
            } finally {
                if (doWriteLog) {
                    LoggingReport report = constructReport(
                            timestamp,
                            wrappedObject.getClass().getSimpleName(),
                            method.getName(),
                            args,
                            thrown,
                            returnValue,
                            void.class.equals(method.getReturnType()));
                    String str = JSONMaker.makeJSON(report);

                    try {
                        writer.write(str + System.lineSeparator());
                        writer.flush();
                    } catch (IOException exc) {
                        Log.log(LoggingProxyFactory.class, exc, "Failed to write log report");
                    }
                }
            }

            if (thrown != null) {
                throw thrown;
            } else {
                return returnValue;
            }
        }
    }
}
