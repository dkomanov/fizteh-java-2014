package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class JSONLogCreator {
    private JSONObject jsonObject = new JSONObject();
    private final IdentityHashMap<Object, Boolean> objects = new IdentityHashMap<Object, Boolean>();

    public void writeTimestamp() {
        try {
            jsonObject = jsonObject.put(JSONFieldsNames.TIMESTAMP.name, System.currentTimeMillis());
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot put into JSONObject!");
        }
    }

    public void writeClass(Class<?> clazz) {
        try {
            jsonObject = jsonObject.put(JSONFieldsNames.CLASS.name, clazz.getName());
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot put into JSONObject!");
        }
    }

    public void writeMethod(Method method) {
        try {
            jsonObject = jsonObject.put(JSONFieldsNames.METHOD.name, method.getName());
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot put into JSONObject!");
        }
    }

    public void writeArguments(Object[] arguments) {
        try {
            if (arguments == null) {
                jsonObject = jsonObject.put(JSONFieldsNames.ARGUMENTS.name, new JSONArray());
                return;
            }
            jsonObject = jsonObject.put(JSONFieldsNames.ARGUMENTS.name, makeJSONArray(Arrays.asList(arguments)));
            objects.clear();
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot put into JSONObject!");
        }
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
        try {
            jsonObject = jsonObject.put(JSONFieldsNames.RETURN_VALUE.name, toWrite);
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot put into JSONObject!");
        }
    }

    public void writeThrown(Throwable cause) {
        try {
            jsonObject = jsonObject.put(JSONFieldsNames.THROWN.name, cause.toString());
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot put into JSONObject!");
        }
    }

    public String getStringRepresentation() {
        String str = null;
        try {
            str = jsonObject.toString(2);
        } catch (JSONException e) {
            System.out.println(getClass().getSimpleName() + ": Cannot convert JSONObject into string!");
        }
        return str;
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

