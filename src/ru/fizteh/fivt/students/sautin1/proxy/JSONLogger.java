package ru.fizteh.fivt.students.sautin1.proxy;

import org.json.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

/**
 * Created by sautin1 on 12/15/14.
 */
public class JSONLogger {
    private final JSONObject jsonLog;

    public JSONLogger() {
        jsonLog = new JSONObject();
    }

    public void logMethodCall(Method method, Object[] args, Object target) {
        logTimestamp();
        logTargetClass(target);
        logMethod(method);
        logArguments(args);
    }

    private void logTimestamp() {
        try {
            jsonLog.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            // suppress -> never thrown
        }
    }

    private void logTargetClass(Object target) {
        try {
            jsonLog.put("class", target.getClass().getName());
        } catch (JSONException e) {
            // suppress -> never thrown
        }
    }

    private void logMethod(Method method) {
        try {
            jsonLog.put("method", method.getName());
        } catch (JSONException e) {
            // suppress -> never thrown
        }
    }

    private void logArguments(Object[] args) {
        IdentityHashMap<Iterable, Boolean> identityHashMap = new IdentityHashMap<Iterable, Boolean>();
        try {
            if (args != null) {
                jsonLog.put("arguments", logIterable(Arrays.asList(args), identityHashMap));
            } else {
                jsonLog.put("arguments", new JSONArray());
            }
        } catch (JSONException e) {
            // suppress -> never thrown
        }
    }

    private Object logIterable(Iterable iterable, IdentityHashMap<Iterable, Boolean> identityHashMap) {
        if (identityHashMap.containsKey(iterable)) {
            return "cyclic";
        }

        identityHashMap.put(iterable, true);

        JSONArray iterableValues = new JSONArray();
        if (iterable != null) {
            for (Object value : iterable) {
                if (value instanceof Iterable) {
                    iterableValues.put(logIterable((Iterable) value, identityHashMap));
                } else if ((value != null) && (value.getClass().isArray())) {
                    iterableValues.put(value.toString());
                } else {
                    iterableValues.put(value);
                }
            }
        }

        return iterableValues;
    }

    public void logReturnValue(Object returnValue) {

        IdentityHashMap<Iterable, Boolean> identityHashMap = new IdentityHashMap<Iterable, Boolean>();
        try {
            if (returnValue == null) {
                jsonLog.put("returnValue", JSONObject.NULL);
            } else if (!(returnValue instanceof Iterable)) {
                jsonLog.put("returnValue", returnValue);
            } else {
                jsonLog.put("returnValue", logIterable((Iterable) returnValue, identityHashMap));
            }
        } catch (JSONException e) {
            // suppress -> never thrown
        }
    }

    public void logThrown(Throwable thrown) {
        try {
            jsonLog.put("thrown", thrown.toString());
        } catch (JSONException e) {
            // suppress -> never thrown
        }
    }

    // used only for tests
    public JSONObject getResultObject() {
        return jsonLog;
    }

    @Override
    public String toString() {
        String result = null;
        try {
            result = (jsonLog.toString(2) + System.getProperty("line.separator"));
        } catch (JSONException e) {
            // suppress
        }
        return result;
    }
}
