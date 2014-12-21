package ru.fizteh.fivt.students.standy66_new.proxy;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.IdentityHashMap;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class JSONLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new ProxyHandler(writer, implementation));

    }

    private static class ProxyHandler implements InvocationHandler {
        private Writer writer;
        private Object implementation;

        public ProxyHandler(Writer writer, Object implementation) {
            this.writer = writer;
            this.implementation = implementation;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            JSONObject log = new JSONObject();
            log.put("timestamp", System.currentTimeMillis());
            log.put("class", implementation.getClass().getName());
            log.put("method", method.getName());
            if (args != null) {
                log.put("arguments", unwindIterable(Arrays.asList(args), new IdentityHashMap<>()));
            }
            Throwable targetException = null;
            Object returnValue = null;
            try {
                returnValue = method.invoke(implementation, args);
            } catch (InvocationTargetException e) {
                targetException = e.getTargetException();
            }

            if (method.getReturnType() != void.class) {
                if (returnValue instanceof Iterable) {
                    log.put("returnValue", unwindIterable((Iterable) returnValue, new IdentityHashMap<>()));
                } else {
                    log.put("returnValue", returnValue);
                }
            }
            if (targetException != null) {
                log.put("thrown", targetException.getClass().getName());
            }
            synchronized (writer) {
                try {
                    log.write(writer);
                    writer.write(String.format("%n"));
                } catch (Exception e) {
                    boolean veryBad = true;
                }
            }
            if (targetException != null) {
                throw targetException;
            }
            return returnValue;
        }

        private JSONArray unwindIterable(Iterable iterable, IdentityHashMap<Object, Object> processed) {
            JSONArray result = new JSONArray();
            iterable.forEach((obj) -> {
                if (processed.containsKey(obj)) {
                    result.put("cyclic");
                } else {
                    if (obj instanceof Iterable) {
                        IdentityHashMap<Object, Object> copy = new IdentityHashMap<>(processed);
                        copy.put(obj, obj);
                        result.put(unwindIterable((Iterable) obj, copy));
                    } else {
                        result.put(obj);
                    }
                }
            });
            return result;
        }

    }
}
