package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class Wrapper implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {

        if (writer == null) {
            throw new IllegalArgumentException(getClass().getSimpleName() + "Writer cannot be null!");
        }

        if (interfaceClass == null) {
            throw new IllegalArgumentException(getClass().getSimpleName() + "Interface class cannot be null!");
        }

        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + "Target object does not implementing interface class!");
        }

        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + "Interface class is not exactly interface!");
        }

        return Proxy.newProxyInstance(
                implementation.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new ProxyInvocationHandler(writer, implementation));
    }
}
