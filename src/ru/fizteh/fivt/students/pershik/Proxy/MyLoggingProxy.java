package ru.fizteh.fivt.students.pershik.Proxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by pershik on 11/26/14.
 */
public class MyLoggingProxy implements InvocationHandler{
    private Writer writer;
    private Object obj;
    Set<Method> objectMethods;

    public MyLoggingProxy(Object obj, Writer writer) {
        this.obj = obj;
        this.writer = writer;
        objectMethods = new HashSet<>();
        Collections.addAll(objectMethods, Object.class.getMethods());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object res = null;
        try {
            JSONObject jsonObj = new JSONObject();
            if (!objectMethods.contains(method)) {
                putTimeStamp(jsonObj);
                putClassName(jsonObj, obj);
                putMethodName(jsonObj, method);
                putArguments(jsonObj, args);
            }
            Throwable exception = null;
            try {
                res = method.invoke(obj, args);
            } catch (InvocationTargetException e) {
                exception = e.getTargetException();
            }
            if (exception != null) {
                putException(jsonObj, exception);
                printJSON(jsonObj);
                throw exception;
            } else {
                putReturnValue(jsonObj, method, res);
                printJSON(jsonObj);
                return res;
            }
        } catch (JSONException e) {
            // Do nothing
        }
        return res;
    }

    private void putTimeStamp(JSONObject jsonObj)
            throws JSONException {
        jsonObj.put("timestamp", System.currentTimeMillis());
    }

    private void putClassName(JSONObject jsonObj, Object obj)
            throws JSONException {
        String className = obj.getClass().toString();
        className = className.substring(6);
        jsonObj.put("class", className);
    }

    private void putMethodName(JSONObject jsonObj, Method method)
            throws JSONException {
        jsonObj.put("method", method.getName());
    }

    private void putArguments(JSONObject jsonObj, Object[] args)
            throws JSONException {
        if (args != null) {
            List<Object> argumentsList = new ArrayList<>(Arrays.asList(args));
            jsonObj.put("arguments",
                    searchCyclicLinks((Iterable) argumentsList, new IdentityHashMap<>()));
        } else {
            jsonObj.put("arguments", new ArrayList<>());
        }
    }

    private void putReturnValue(JSONObject jsonObj, Method method, Object res)
            throws JSONException {
        if (method.getReturnType() != void.class) {
            if (res instanceof Iterable) {
                JSONArray arr =
                        searchCyclicLinks((Iterable) res, new IdentityHashMap<>());
                jsonObj.put("returnValue", arr);
            } else {
                if (res == null) {
                    jsonObj.put("returnValue", JSONObject.NULL);
                } else {
                    jsonObj.put("returnValue", res);
                }
            }
        }
    }

    private void putException(JSONObject jsonObj, Throwable exception)
            throws JSONException {
        jsonObj.put("thrown", exception);
    }

    private JSONArray searchCyclicLinks(Iterable list, IdentityHashMap<Object, Object> map)
            throws JSONException {
        map.put(list, null);
        JSONArray res = new JSONArray();
        for (Object iter : list) {
            if (iter instanceof Iterable) {
                if (map.containsKey(iter)) {
                    res.put("cyclic");
                } else {
                    res.put(searchCyclicLinks((Iterable) iter, map));
                }
            } else {
                res.put(iter);
            }
        }
        map.remove(list);
        return res;
    }

    private void printJSON(JSONObject jsonObj) {
        try {
            writer.write(jsonObj.toString());
        } catch (IOException e) {
            // Do nothing
        }
    }
}
