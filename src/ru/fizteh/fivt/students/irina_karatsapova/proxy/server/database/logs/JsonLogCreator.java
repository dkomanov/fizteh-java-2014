package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JsonLogCreator {
    static final int NUMBER_OF_SPACES = 4;

    private JSONObject jsonLog = new JSONObject();

    JsonLogCreator() {
    }

    void writeTimestamp() throws JSONException {
        jsonLog.put("timestamp", System.currentTimeMillis());
    }

    void writeClass(Class<?> clazz) throws JSONException {
        jsonLog.put("class", clazz.getName());
    }

    void writeMethod(Method method) throws JSONException {
        jsonLog.put("method", method.getName());
    }

    void writeThrown(Throwable e) throws JSONException {
        jsonLog.put("thrown", e.toString());
    }

    void writeArguments(Object[] args) throws JSONException {
        if (args == null) {
            jsonLog.put("arguments", new JSONArray());
        } else {
            jsonLog.put("arguments", constructJsonArray(Arrays.asList(args), new IdentityHashMap<>()));
        }
    }

    void writeReturnValue(Object result) throws JSONException {
        if (result instanceof Iterable) {
            jsonLog.put("returnValue", constructJsonArray((Iterable) result, new IdentityHashMap<>()));
        } else {
            jsonLog.put("returnValue", jsonLog.wrap(result));
        }
    }

    String getLog() throws JSONException {
        return jsonLog.toString(NUMBER_OF_SPACES);
    }

    private JSONArray constructJsonArray(Iterable args, IdentityHashMap<Object, Object> alreadySeenArgs) {
        JSONArray jsonArray = new JSONArray();
        if (args == null) {
            jsonArray.put(args);
            return jsonArray;
        }
        for (Object arg: args) {
            if (alreadySeenArgs.containsKey(arg)) {
                jsonArray.put("cyclic");
            } else if (arg instanceof Iterable) {
                alreadySeenArgs.put(arg, null);
                jsonArray.put(constructJsonArray(((Iterable) arg), new IdentityHashMap<>(alreadySeenArgs)));
            } else {
                jsonArray.put(jsonLog.wrap(arg));
            }
        }
        return jsonArray;
    }
}
