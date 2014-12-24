package ru.fizteh.fivt.students.moskupols.proxy;

import org.json.JSONArray;
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

    @Override
    public String serialize(Method method, Object[] args, Object returnValue) {
        JSONObject obj = new JSONObject();

        obj.put("timestamp", System.currentTimeMillis());
        obj.put("class", method.getDeclaringClass().getCanonicalName());
        obj.put("method", method.getName());
        obj.put("arguments", jsonify(args));
        if (!method.getReturnType().equals(void.class)) {
            obj.put("returnValue", jsonify(returnValue));
        }

        return obj.toString();
    }
}
