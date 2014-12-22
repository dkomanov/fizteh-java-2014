
package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import com.sun.istack.internal.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by luba_yaronskaya on 01.12.14.
 */
public class RealInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object implementation;

    public RealInvocationHandler(Writer writer, Object implementation) {
        this.writer = writer;
        this.implementation = implementation;
    }

    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Method objectMethod : Object.class.getMethods()) {
            if (method.equals(objectMethod)) {
                return method.invoke(implementation, args);
            }
        }
        JSONObject log = new JSONObject();
        log.put("timestamp", System.currentTimeMillis());
        log.put("class", implementation.getClass().getName());
        log.put("method", method.getName());
        log.put("arguments", args == null ? new JSONArray() : getJSONArray(Arrays.asList(args), new IdentityHashMap<>()));
        Throwable thrownException = null;
        Object returnValue = null;
        try {
            returnValue = method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            thrownException = e.getTargetException();
        }
        if (thrownException != null) {
            log.put("thrown", thrownException.toString());
        } else {
            if (method.getReturnType() != void.class) {
                if (returnValue instanceof Iterable) {
                    log.put("returnValue", getJSONArray((Iterable) returnValue, new IdentityHashMap<>()));
                } else {
                    log.put("returnValue", returnValue);
                }
            }
        }

        writer.write(log.toString());

        return returnValue;
    }

    public JSONArray getJSONArray(Iterable args, IdentityHashMap<Object, Object> values) {
        JSONArray res = new JSONArray();

        for (Object val : args) {
            if (values.containsKey(val)) {
                res.put("cyclic");
            } else {
                IdentityHashMap<Object, Object> valuesUpdate = values;
                if (val instanceof Iterable) {
                    valuesUpdate.put(val, val);
                    res.put(getJSONArray((Iterable) val, valuesUpdate));
                } else {
                    res.put(val.toString());
                }
            }
        }
        return res;
    }

}