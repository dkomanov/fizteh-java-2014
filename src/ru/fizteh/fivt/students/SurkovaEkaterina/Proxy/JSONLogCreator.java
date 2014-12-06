package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JSONLogCreator {
    private JSONObject jsonObject = new JSONObject();
    private final IdentityHashMap<Object, Boolean> objects = new IdentityHashMap<Object, Boolean>();

    public void writeTimestamp() {
        jsonObject = jsonObject.put(JSONFieldsNames.TIMESTAMP.name, System.currentTimeMillis());
    }

    public void writeClass(Class<?> clazz) {
        jsonObject = jsonObject.put(JSONFieldsNames.CLASS.name, clazz.getName());
    }

    public void writeMethod(Method method) {
        jsonObject = jsonObject.put(JSONFieldsNames.METHOD.name, method.getName());
    }

    public void writeArguments(Object[] arguments) {
        if (arguments == null) {
            jsonObject = jsonObject.put(JSONFieldsNames.ARGUMENTS.name, new JSONArray());
            return;
        }
        jsonObject = jsonObject.put(JSONFieldsNames.ARGUMENTS.name, makeJSONArray(Arrays.asList(arguments)));
        objects.clear();
    }

    public void writeReturnValue(Object returnValue) {
        Object toWrite;
        if (returnValue != null) {
            if (returnValue instanceof Iterable) {
                toWrite = makeJSONArray((Iterable) returnValue);
            } else {
                toWrite = returnValue;
            }
        } else {
            toWrite = JSONObject.NULL;
        }
        jsonObject = jsonObject.put(JSONFieldsNames.RETURN_VALUE.name, toWrite);
    }

    public void writeThrown(Throwable cause) {
        jsonObject = jsonObject.put(JSONFieldsNames.THROWN.name, cause.toString());
    }

    public String getStringRepresentation() {
        return jsonObject.toString(2);
    }

    private JSONArray makeJSONArray(Iterable collection) {
        JSONArray result = new JSONArray();
        for (Object value : collection) {
            if (value == null) {
                result.put(value);
                continue;
            }

            if (value.getClass().isArray()) {
                result.put(value.toString());
                continue;
            }

            boolean isContainer = false;
            boolean isEmpty = false;

            if (value instanceof Iterable) {
                isContainer = true;
                isEmpty = !((Iterable) value).iterator().hasNext();
            }

            if (objects.containsKey(value) && isContainer && !isEmpty) {

                result.put(JSONFieldsNames.CYCLIC.name);
                continue;
            }

            objects.put(value, true);

            if (isContainer) {
                result.put(makeJSONArray((Iterable) value));
                continue;
            }

            result.put(value);
        }
        return result;
    }
}

