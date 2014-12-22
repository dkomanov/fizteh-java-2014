package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by akhtyamovpavel on 30.11.14.
 */
public class LoggerFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        Class<?>[] interfaces = new Class[1];
        interfaces[0] = interfaceClass;
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), interfaces,
                new Logger(implementation, writer));
    }
}
