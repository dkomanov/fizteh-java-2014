package ru.fizteh.fivt.students.lukina.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbProxyHandler implements InvocationHandler {
    Writer writer;
    Object implementation;
    Lock lock = new ReentrantLock(true);

    public DbProxyHandler(Writer wr, Object implement) {
        writer = wr;
        implementation = implement;
    }

    @Override
    public Object invoke(Object inputClass, Method method, Object[] arguments)
            throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(implementation, arguments);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        Object result = null;
        Throwable exception = null;
        try {
            result = method.invoke(implementation, arguments);
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
        }
        JSONObject log = logging(method, arguments, exception, result);
        try {
            writer.write(log.toString() + System.lineSeparator());
        } catch (Throwable e) {
            // ignore
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    private JSONObject logging(Method method, Object[] arguments,
                               Throwable exception, Object result) {
        try {
            JSONObject log = new JSONObject();
            log.put("timestamp", System.currentTimeMillis());
            log.put("class", implementation.getClass().getName());
            log.put("method", method.getName());
            if (arguments == null || arguments.length == 0) {
                log.put("arguments", new Object[0]);
            } else {
                log.put("arguments",
                        createJSONArray(arguments, new IdentityHashMap<>()));
            }
            if (exception != null) {
                log.put("thrown", exception.toString());
                return log;
            }
            if (method.getReturnType().equals(void.class)) {
                return log;
            }
            Object value = null;
            if (result == null) {
                value = JSONObject.NULL;
            } else {
                if (Iterable.class.isAssignableFrom(result.getClass())) {
                    value = createJSONArrayIterable((Iterable<?>) result,
                            new IdentityHashMap<>());
                } else if (result.getClass().isArray()) {
                    value = createJSONArray((Object[]) result,
                            new IdentityHashMap<>());
                } else {
                    value = result;
                }
            }
            log.put("returnValue", value);
            return log;
        } catch (Throwable e) {
            // ignore
        }
        return null;
    }

    private void addObjectInArray(JSONArray jsonArray, Object arg,
                                  IdentityHashMap<Object, Object> addedElements) {
        if (arg == null) {
            jsonArray.put(arg);
            return;
        }
        if (addedElements.containsKey(arg)) {
            jsonArray.put("cyclic");
            return;
        }
        if (Iterable.class.isAssignableFrom(arg.getClass())) {
            jsonArray.put(createJSONArrayIterable((Iterable<?>) arg,
                    addedElements));
            return;
        }
        if (arg.getClass().isArray()) {
            try {
                jsonArray.put(createJSONArray((Object[]) arg, addedElements));
            } catch (ClassCastException e) {
                jsonArray.put(arg.toString());
            }
            return;
        }
        jsonArray.put(arg);
    }

    private JSONArray createJSONArray(Object[] array,
                                      IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object arg : array) {
            addObjectInArray(jsonArray, arg, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }

    private JSONArray createJSONArrayIterable(Iterable<?> array,
                                              IdentityHashMap<Object, Object> addedElements) {
        addedElements.put(array, null);
        JSONArray jsonArray = new JSONArray();
        for (Object arg : array) {
            addObjectInArray(jsonArray, arg, addedElements);
        }
        addedElements.remove(array);
        return jsonArray;
    }
}
