package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author AlexeyZhuravlev
 */
public class MyLoggingProxyInvocationHandler implements InvocationHandler {

    private Writer log;
    private Object target;

    public MyLoggingProxyInvocationHandler(Writer writer, Object implementation) {
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
            for (Object argument: args) {
                arguments.put(JsonConverter.convertObject(argument));
            }
            object.put("arguments", arguments);
            if (method.getReturnType() == void.class) {
                writeToLog(object);
                return method.invoke(target, args);
            } else {
                Object result = method.invoke(target, args);
                object.put("returnValue", JsonConverter.convertObject(result));
                writeToLog(object);
                return result;
            }
        } catch (Exception e) {
            object.put("thrown", e);
            writeToLog(object);
            throw new InvocationTargetException(e);
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
