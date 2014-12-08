package ru.fizteh.fivt.students.lukina.proxy;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LoggingTest {
    DbLoggingProxyFactory factory;
    LoggingTestInterface wrapped;
    StringWriter writer;
    StringBuffer str;

    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        factory = new DbLoggingProxyFactory();
        LoggingTestClass implementation = new LoggingTestClass();
        wrapped = (LoggingTestInterface) factory.wrap(writer, implementation,
                LoggingTestInterface.class);
    }

    @Test
    public void takeIntReturnVoidTest() {
        wrapped.takeIntReturnVoid(8);
        try {
            JSONObject log = new JSONObject(writer.toString());
            String rightResult = "{\"timestamp\":1385753633506,\"arguments\":[8],"
                    + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                    + "\"method\":\"takeIntReturnVoid\"}";
            JSONObject rightLog = null;
            rightLog = new JSONObject(rightResult);

            rightLog.put("timestamp", log.get("timestamp"));
            assertEquals("incorrect log", log.toString(), rightLog.toString());
        } catch (JSONException j) {
            //not OK
        }
    }

    @Test(expected = Exception.class)
    public void takeStringThrowExceptionTest() throws Exception {
        Exception exc = null;
        try {
            wrapped.takeStringThrowException("lala");
        } catch (Exception e) {
            exc = e;
        }
        JSONObject log = new JSONObject(writer.toString());
        String rightResult = "{\"timestamp\":1385753633506,"
                + "\"thrown\":\"java.io.IOException: Ooops\",\"arguments\":[\"lala\"],"
                + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                + "\"method\":\"takeStringThrowException\"}";
        JSONObject rightLog = new JSONObject(rightResult);
        rightLog.put("timestamp", log.get("timestamp"));
        assertEquals("incorrect log", log.toString(), rightLog.toString());
        throw exc;
    }

    @Test(expected = Exception.class)
    public void takeNothingReturnVoidThrowExceptionTest() throws Exception {
        Exception exc = null;
        try {
            wrapped.takeNothingReturnVoidThrowException();
        } catch (Exception e) {
            exc = e;
        }
        JSONObject log = new JSONObject(writer.toString());
        String rightResult = "{\"timestamp\":1385753633506,"
                + "\"thrown\":\"java.lang.RuntimeException: I love this test!\",\"arguments\":[],"
                + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                + "\"method\":\"takeNothingReturnVoidThrowException\"}";
        JSONObject rightLog = new JSONObject(rightResult);
        rightLog.put("timestamp", log.get("timestamp"));
        assertEquals("incorrect log", log.toString(), rightLog.toString());
        throw exc;
    }

    @Test
    public void takeIterableReturnArraySimpleTest() {
        try {
            ArrayList<?> list = new ArrayList<>();
            wrapped.takeIterableReturnArray(list);
            JSONObject log = new JSONObject(writer.toString());
            String rightResult = "{\"timestamp\":1385753633506,\"arguments\":[[]],\"returnValue\":[],"
                    + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                    + "\"method\":\"takeIterableReturnArray\"}";
            JSONObject rightLog = new JSONObject(rightResult);
            rightLog.put("timestamp", log.get("timestamp"));
            assertEquals("incorrect log", log.toString(), rightLog.toString());
        } catch (JSONException j) {
            //not OK
        }
    }

    @Test
    public void takeIterableReturnArrayCyclicTest() {
        try {
            ArrayList<Object> list = new ArrayList<>(2);
            list.add(2);
            list.add(list);
            list.add(3);
            wrapped.takeIterableReturnArray(list);
            JSONObject log = new JSONObject(writer.toString());
            String rightResult = "{\"timestamp\":1385753633506,\"arguments\":[[2,\"cyclic\",3]],\"returnValue\":[],"
                    + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                    + "\"method\":\"takeIterableReturnArray\"}";
            JSONObject rightLog = new JSONObject(rightResult);
            rightLog.put("timestamp", log.get("timestamp"));
            assertEquals("incorrect log", log.toString(), rightLog.toString());
        } catch (JSONException j) {
            //not OK
        }
    }

    @Test
    public void takeNothingReturnNothingTest() {
        try {
            wrapped.takeNothingReturnNothing();
            JSONObject log = new JSONObject(writer.toString());
            String rightResult = "{\"timestamp\":1385753633506,\"arguments\":[],"
                    + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                    + "\"method\":\"takeNothingReturnNothing\"}";
            JSONObject rightLog = new JSONObject(rightResult);
            rightLog.put("timestamp", log.get("timestamp"));
            assertEquals("incorrect log", log.toString(), rightLog.toString());
        } catch (JSONException j) {
            //not OK
        }
    }

    @Test
    public void takeNothingReturnInt() {
        try {
            wrapped.takeNothingReturnInt();
            JSONObject log = new JSONObject(writer.toString());
            String rightResult = "{\"timestamp\":1385753633506,\"arguments\":[],\"returnValue\":17,"
                    + "\"class\":\"ru.fizteh.fivt.students.lukina.proxy.LoggingTestClass\","
                    + "\"method\":\"takeNothingReturnInt\"}";
            JSONObject rightLog = new JSONObject(rightResult);
            rightLog.put("timestamp", log.get("timestamp"));
            assertEquals("incorrect log", log.toString(), rightLog.toString());
        } catch (JSONException j) {
            //not OK
        }
    }

    @After
    public void close() throws IOException {
        writer.close();
    }
}
