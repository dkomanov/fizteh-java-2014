package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.ProxyPackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class RealLogProxyFactory implements LoggingProxyFactory {
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
        private Object implementation;
        public DebugProxy(Writer writer, Object implementation) {
            this.writer = writer;
            this.implementation = implementation;
        }

        @Override
        public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
            JSONObject logObject = new JSONObject();
            logObject.put("timestamp", System.currentTimeMillis());
            logObject.put("class", implementation.getClass().getName());
            logObject.put("method", method.getName());
            logObject.put("arguments", deepIntoIterebleArg(Arrays.asList(args), new IdentityHashMap<>()));
            Object result = null;
            Throwable exceptionThatWillBeThrown = null;
            try {
                result = method.invoke(obj, args);
            } catch (InvocationTargetException e) {
                exceptionThatWillBeThrown = e.getTargetException();
            } catch (Exception e) {
                //suppress
            }
            if (method.getReturnType() != Void.class) {
                if (result instanceof Iterable) {
                    logObject.put("returnValue", deepIntoIterebleArg(Arrays.asList(result), new IdentityHashMap<>()));
                } else {
                    logObject.put("returnValue", result);
                }
            }
            if (exceptionThatWillBeThrown != null) {
                logObject.put("thrown", exceptionThatWillBeThrown.toString());
            }

            synchronized (writer) {
                try {
                    logObject.write(writer);
                } catch (JSONException e) {
                    //suppress
                }
            }

            writer.write("\n");
            if (exceptionThatWillBeThrown != null) {
                throw exceptionThatWillBeThrown;
            }
            return result;
        }

        private JSONArray deepIntoIterebleArg(Iterable iterable, IdentityHashMap<Object, Object> processed) {
            JSONArray result = new JSONArray();
            for (Object oneArg : iterable) {
                if (processed.containsKey(oneArg)) {
                    result.put("cyclic");
                } else {
                    if (oneArg instanceof Iterable) {
                        IdentityHashMap<Object, Object> copy = new IdentityHashMap<>(processed);
                        copy.put(oneArg, oneArg);
                        result.put(deepIntoIterebleArg((Iterable) oneArg, copy));
                    } else {
                        result.put(oneArg);
                    }
                }
            }
            return result;
        }
    }
}
