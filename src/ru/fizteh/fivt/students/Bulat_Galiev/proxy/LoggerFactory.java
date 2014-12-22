package ru.fizteh.fivt.students.Bulat_Galiev.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class LoggerFactory implements LoggingProxyFactory {
    @Override
    public final Object wrap(final Writer writer, final Object implementation,
            final Class<?> interfaceClass) {
        Class<?>[] interfaces = new Class[1];
        interfaces[0] = interfaceClass;
        return Proxy.newProxyInstance(implementation.getClass()
                .getClassLoader(), interfaces, new Logger(implementation,
                writer));
    }
}
