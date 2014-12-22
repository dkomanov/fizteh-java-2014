package ru.fizteh.fivt.students.ryad0m.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class LoggingInvocationHandler implements InvocationHandler {

    private Writer log;
    private Object target;

    public LoggingInvocationHandler(Writer writer, Object implementation) {
        log = writer;
        target = implementation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JSONObject object = new JSONObject();
        try {
            object.put("timestamp", System.currentTimeMillis());
            object.put("class", target.getClass().getName());
            object.put("method", method.getName());
            JSONArray arguments = new JSONArray();
            if (args != null) {
                for (Object argument : args) {
                    arguments.put(JsonConverter.convertObject(argument));
                }
            }
            object.put("arguments", arguments);
            if (method.getReturnType() == void.class) {
                method.invoke(target, args);
                writeToLog(object);
                return null;
            } else {
                Object result = method.invoke(target, args);
                object.put("returnValue", JsonConverter.convertObject(result));
                writeToLog(object);
                return result;
            }
        } catch (InvocationTargetException e) {
            object.put("thrown", e.getTargetException());
            writeToLog(object);
            throw e;
        }
    }

    private void writeToLog(JSONObject object) {
        try {
            log.write(object.toString());
        } catch (IOException e) {
            // Do nothing
        }
    }
}
