package ru.fizteh.fivt.students.pershik.Proxy;

import org.json.JSONObject;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res = null;
        JSONObject jsonObj = new JSONObject();
        if (!objectMethods.contains(method)) {
            jsonObj.put("timestamp", System.currentTimeMillis());
            jsonObj.put("class", obj.getClass());
            jsonObj.put("method", method.getName());
            // TODO Add arguments
        }
        Throwable exception = null;
        try {
            res = method.invoke(obj, args);
        } catch (InvocationTargetException e) {
            jsonObj.put("thrown", e.getTargetException());
            exception = e.getTargetException();
        }
        if (exception != null) {
            System.out.println(jsonObj.toString());
            throw exception;
        } else {
            Class<?> cl = method.getReturnType();
            if (method.getReturnType() != Void.class) {
                jsonObj.put("returnValue", res);
            }
            System.out.println(jsonObj.toString());
            return res;
        }
    }

}
