package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

/**
 * Created by luba_yaronskaya on 01.12.14.
 */
public class RealLoggingProxyFactory implements LoggingProxyFactory {

    @Override
    public Object wrap(
            Writer writer,
            Object implementation,
            Class<?> interfaceClass
    ) {
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("arguments for wrap must be not null");
        }

        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass}, new RealInvocationHandler(writer, implementation));
    }
}
