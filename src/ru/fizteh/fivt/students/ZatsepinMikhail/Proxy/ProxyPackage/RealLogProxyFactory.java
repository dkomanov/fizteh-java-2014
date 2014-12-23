package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.ProxyPackage;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import javax.json.*;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.IdentityHashMap;

public class RealLogProxyFactory implements LoggingProxyFactory {
    public RealLogProxyFactory() {}

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) throws IllegalArgumentException {
        if (writer == null || implementation == null || interfaceClass == null) {
            throw new IllegalArgumentException("null argument");
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
                new Class<?>[]{interfaceClass}, new DebugProxy(writer, implementation));
    }

    public class DebugProxy implements InvocationHandler {
        private Writer writer;
        private Object implementation;

        public DebugProxy(Writer writer, Object implementation) {
            this.writer = writer;
            this.implementation = implementation;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Method oneMethod : Object.class.getMethods()) {
                if (oneMethod.equals(method)) {
                    return method.invoke(implementation, args);
                }
            }
            JsonObjectBuilder logObject = Json.createObjectBuilder()
                    .add("timestamp", System.currentTimeMillis())
                    .add("class", implementation.getClass().getName())
                    .add("method", method.getName());
            if (args != null) {
                JsonArrayBuilder arguments = Json.createArrayBuilder();
                for (Object oneArg : args) {
                    if (oneArg instanceof Iterable) {
                        arguments.add(deepIntoIterableArg((Iterable) oneArg, new IdentityHashMap<>()));
                    } else {
                        addNewValueArray(arguments, oneArg);
                    }
                }
                logObject.add("arguments", arguments.build());
            } else {
                logObject.add("arguments", Json.createArrayBuilder().build());
            }

            Object result = null;
            Throwable exceptionThatWillBeThrown = null;
            try {
                result = method.invoke(implementation, args);
            } catch (InvocationTargetException e) {
                exceptionThatWillBeThrown = e.getTargetException();
                logObject.add("thrown", exceptionThatWillBeThrown.toString());
            }

            if (method.getReturnType() != void.class & exceptionThatWillBeThrown == null) {
                if (result instanceof Iterable) {
                    logObject.add("returnValue", deepIntoIterableArg((Iterable) result, new IdentityHashMap<>()));
                } else {
                    addNewValueObject(logObject, "returnValue", result);
                }
            }

            synchronized (writer) {
                JsonWriter writerJson = Json.createWriter(writer);
                writerJson.writeObject(logObject.build());
            }

            writer.write("\n");
            if (exceptionThatWillBeThrown != null) {
                throw exceptionThatWillBeThrown;
            }
            return result;
        }


        private JsonArrayBuilder deepIntoIterableArg(Iterable iterable, IdentityHashMap<Object, Object> processed) {
            JsonArrayBuilder result = Json.createArrayBuilder();
            for (Object oneArg : iterable) {
                if (processed.containsKey(oneArg)) {
                    result.add("cyclic");
                } else {
                    if (oneArg instanceof Iterable) {
                        IdentityHashMap<Object, Object> copy = new IdentityHashMap<>(processed);
                        copy.put(oneArg, oneArg);
                        result.add(deepIntoIterableArg((Iterable) oneArg, copy));
                    } else {
                        if (oneArg != null) {
                            addNewValueArray(result, oneArg);
                        } else {
                            result.addNull();
                        }
                    }
                }
            }
            return result;
        }

        private void addNewValueObject(JsonObjectBuilder logObject, String category, Object value) {
            if (value == null) {
                logObject.addNull(category);
                return;
            }
            if (value.getClass().equals(Long.class)) {
                logObject.add(category, (long) value);
                return;
            }
            if (value.getClass().equals(Byte.class)) {
                logObject.add(category, (byte) value);
                return;
            }
            if (value.getClass().equals(Float.class)) {
                logObject.add(category, (float) value);
                return;
            }
            if (value.getClass().equals(Double.class)) {
                logObject.add(category, (double) value);
                return;
            }
            if (value.getClass().equals(Boolean.class)) {
                logObject.add(category, (boolean) value);
                return;
            }
            if (value.getClass().equals(Integer.class)) {
                logObject.add(category, (int) value);
                return;
            }
            logObject.add(category, value.toString());
        }

        private void addNewValueArray(JsonArrayBuilder logObject, Object value) {
            if (value == null) {
                logObject.addNull();
                return;
            }
            if (value.getClass().equals(Integer.class)) {
                logObject.add((int) value);
                return;
            }
            if (value.getClass().equals(Long.class)) {
                logObject.add((long) value);
                return;
            }
            if (value.getClass().equals(Byte.class)) {
                logObject.add((byte) value);
                return;
            }
            if (value.getClass().equals(Float.class)) {
                logObject.add((float) value);
                return;
            }
            if (value.getClass().equals(Double.class)) {
                logObject.add((double) value);
                return;
            }
            if (value.getClass().equals(Boolean.class)) {
                logObject.add((boolean) value);
                return;
            }
            logObject.add(value.toString());
        }
    }
}
