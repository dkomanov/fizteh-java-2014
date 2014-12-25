package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by moskupols on 24.12.14.
 */
public class LoggingProxyFactoryImpl implements LoggingProxyFactory, SingleWriterLoggingProxyFactory {
    private Writer defaultWriter;

    public LoggingProxyFactoryImpl() {
        this(null);
    }

    public LoggingProxyFactoryImpl(Writer defaultWriter) {
        this.defaultWriter = defaultWriter;
    }

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new NullPointerException();
        }
        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[] {interfaceClass },
                new LoggingInvocationHandler(
                        writer, new JsonInvocationSerializer(), implementation, interfaceClass));
    }

    @Override
    public Object wrap(Object implementation, Class<?> interfaceClass) {
        return wrap(defaultWriter, implementation, interfaceClass);
    }
}
