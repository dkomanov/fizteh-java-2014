package ru.fizteh.fivt.students.moskupols.proxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

/**
 * Created by moskupols on 24.12.14.
 */
public class JsonInvocationSerializer implements InvocationSerializer {
    protected Object jsonify(Object obj) {
        return jsonify(obj, new IdentityHashMap<>());
    }

    protected Object jsonify(Object obj, IdentityHashMap<Object, Boolean> processed) {
        if (processed.containsKey(obj)) {
            return "cyclic";
        }
        processed.put(obj, true);
        if (obj instanceof Array) {
            obj = Arrays.asList(obj);
        }
        if (obj instanceof Iterable) {
            JSONArray ret = new JSONArray();
            for (Object o : ((Iterable) obj)) {
                ret.put(jsonify(o, processed));
            }
            return ret;
        }
        if (obj == null) {
            return JSONObject.NULL;
        }
        return obj;
    }

    protected Object jsonifyThrown(Throwable t) {
        return t.getClass().getCanonicalName() + ": " + t.getMessage();
    }

    @Override
    public String serialize(
            Method method, Object[] args, Class<?> implClass, Object returnValue, Throwable thrown) {
        JSONObject obj = new JSONObject();
        final String ret;

        try {
            obj.put("timestamp", System.currentTimeMillis()).
                    put("class", implClass.getCanonicalName()).
                    put("method", method.getName()).
                    put("arguments", jsonify(args));
            if (thrown != null) {
                obj.put("thrown", jsonifyThrown(thrown));
            } else if (!method.getReturnType().equals(void.class)) {
                obj.put("returnValue", jsonify(returnValue));
            }
            ret = obj.toString(4);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }
}
