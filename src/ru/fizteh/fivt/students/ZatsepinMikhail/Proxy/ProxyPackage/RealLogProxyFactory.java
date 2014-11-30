package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.ProxyPackage;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RealLogProxyFactory implements LoggingProxyFactory {
    Object innerObject;

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) throws IllegalArgumentException {
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("null argument");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                implementation.getClass().getInterfaces(), new DebugProxy(writer, implementation));
    }

    public static class DebugProxy implements InvocationHandler {
        private Writer writer;
        private Object implemetation;
        public DebugProxy(Writer writer, Object implemetation) {
            this.writer = writer;
            this.implemetation = implemetation;
        }

        @Override
        public Object invoke(Object obj, Method m, )

    }


    public static Object newInstance(Object obj) {
            return java.lang.reflect.Proxy.newProxyInstance(
                    obj.getClass().getClassLoader(),
                    obj.getClass().getInterfaces(),
                    new DebugProxy(obj));
        }

        private DebugProxy(Object obj) {
            this.obj = obj;
        }

        public Object invoke(Object proxy, Method m, Object[] args)
        throws Throwable
        {
            Object result;
            try {
                System.out.println("before method " + m.getName());
                result = m.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " +
                        e.getMessage());
            } finally {
                System.out.println("after method " + m.getName());
            }
            return result;
        }

    }
}
