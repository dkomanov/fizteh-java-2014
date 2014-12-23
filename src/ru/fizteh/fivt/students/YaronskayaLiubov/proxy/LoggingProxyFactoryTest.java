package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luba_yaronskaya on 03.12.14.
 */


public class LoggingProxyFactoryTest {
    TestClass testClass;
    StringWriter writer;


    @Before
    public void setUp() throws Exception {
        writer = new StringWriter();
        testClass = (TestClass) new RealLoggingProxyFactory().wrap(writer, new LoggingTestClass(), TestClass.class);
    }

    @Test
    public void testVoidMethod() throws Exception {
        testClass.voidMethod();
        JSONObject log = new JSONObject(writer.toString());
        assertTrue(log.has("timestamp"));
        assertTrue(log.has("class"));
        assertTrue(log.has("method"));
        assertTrue(log.has("arguments"));
        assertTrue(!log.has("returnValue"));
        assertTrue(!log.has("thrown"));

        assertEquals("voidMethod", log.get("method"));
        assertEquals(0, log.getJSONArray("arguments").length());
    }

    @Test
    public void testIntMethodIntInt() throws Exception {
        testClass.intMethodIntInt(1, 2);
        JSONObject log = new JSONObject(writer.toString());
        assertTrue(log.has("timestamp"));
        assertTrue(log.has("class"));
        assertTrue(log.has("method"));
        assertTrue(log.has("arguments"));
        assertTrue(log.has("returnValue"));
        assertTrue(!log.has("thrown"));

        assertEquals("intMethodIntInt", log.get("method"));
        JSONArray args = log.getJSONArray("arguments");
        assertEquals(2, args.length());
        assertEquals(args.getInt(0), 1);
        assertEquals(args.getInt(1), 2);
        assertEquals(1, log.getInt("returnValue"));
    }

    @Test
    public void testExceptionMethod() throws Exception {
        testClass.exceptionMethod();
        JSONObject log = new JSONObject(writer.toString());
        assertTrue(log.has("timestamp"));
        assertTrue(log.has("class"));
        assertTrue(log.has("method"));
        assertTrue(log.has("arguments"));
        assertTrue(!log.has("returnValue"));
        assertTrue(log.has("thrown"));
        assertEquals("java.io.IOException", log.get("thrown"));
    }

    @Test
    public void testListMethod() throws Exception {

        testClass.listMethod();
        JSONObject log = new JSONObject(writer.toString());
        assertTrue(log.has("timestamp"));
        assertTrue(log.has("class"));
        assertTrue(log.has("method"));
        assertTrue(log.has("arguments"));
        assertTrue(log.has("returnValue"));
        assertTrue(!log.has("thrown"));
        JSONArray list = log.getJSONArray("returnValue");
        assertEquals(2, list.length());
    }

    @Test
    public void testVoidMethodCyclicList() throws Exception {
        List<Object> list = new ArrayList<>();
        list.add("value");
        list.add(list);

        testClass.voidMethodCyclicList(list);
        JSONObject log = new JSONObject(writer.toString());
        assertTrue(log.has("timestamp"));
        assertTrue(log.has("class"));
        assertTrue(log.has("method"));
        assertTrue(log.has("arguments"));
        assertTrue(!log.has("returnValue"));
        assertTrue(!log.has("thrown"));
        JSONArray args = log.getJSONArray("arguments");
        assertEquals(1, args.length());
        assertEquals("value", args.getJSONArray(0).get(0));
        assertEquals("cyclic", args.getJSONArray(0).get(1));
    }
}
