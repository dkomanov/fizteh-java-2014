package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.MyLoggingProxyFactory;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

public class MyLoggingProxyFactoryTest {

    StringWriter writer;
    TestInterface wrappedObject;

    @Before
    public void initStructures() {
        writer = new StringWriter();
        LoggingProxyFactory factory = new MyLoggingProxyFactory();
        TestInterface object = new TestInterfaceImpl();
        wrappedObject = (TestInterface) factory.wrap(writer, object, TestInterface.class);
    }

    @Test
    public void testMethodCall() {
        wrappedObject.noArgumentVoidMethod();
        JSONObject object = new JSONObject(writer.toString());
        assertTrue(object.has("method"));
        assertTrue(object.has("class"));
        assertTrue(object.has("arguments"));
        assertTrue(object.has("timestamp"));
        assertEquals(object.keySet().size(), 4);
        assertEquals(object.get("method"), "noArgumentVoidMethod");
        assertEquals(object.get("class"), "ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.test.TestInterfaceImpl");
        JSONArray args = object.getJSONArray("arguments");
        assertEquals(args.length(), 0);
    }

    @Test
    public void testArgumentMethodCall() {
        wrappedObject.singleArgumentVoidMethod(2);
        JSONObject object = new JSONObject(writer.toString());
        JSONArray args = object.getJSONArray("arguments");
        assertEquals(args.length(), 1);
        assertEquals(args.get(0), 2);
    }

    @Test
    public void twoArgumentsMethodCall() {
        wrappedObject.twoArgumentsVoidMethod("hello", 0);
        JSONObject object = new JSONObject(writer.toString());
        JSONArray args = object.getJSONArray("arguments");
        assertEquals(args.length(), 2);
        assertEquals(args.get(0), "hello");
        assertEquals(args.get(1), 0);
    }

    @Test
    public void noArgumentPrimitiveTypeMethodCall() {
        int result = wrappedObject.noArgumentPrimitiveTypeMethod();
        JSONObject object = new JSONObject(writer.toString());
        assertTrue(object.has("returnValue"));
        assertEquals(object.keySet().size(), 5);
        assertEquals(object.get("returnValue"), result);
    }

    @Test
    public void listArgumentMethodCall() {
        List<Object> list = new Vector<>();
        list.add("a");
        list.add("b");
        list.add("c");
        wrappedObject.listArgumentMethod(list);
        JSONObject object = new JSONObject(writer.toString());
        JSONArray argument = object.getJSONArray("arguments").getJSONArray(0);
        assertEquals("a", argument.getString(0));
        assertEquals("b", argument.getString(1));
        assertEquals("c", argument.getString(2));
    }

    @Test
    public void listArgumentWithListReturn() {
        List<String> list = new Vector<>();
        list.add("a");
        list.add("b");
        wrappedObject.listArgumentWithListReturnTypeMethod(list);
        JSONObject object = new JSONObject(writer.toString());
        JSONArray argument = object.getJSONArray("arguments").getJSONArray(0);
        JSONArray result = object.getJSONArray("returnValue");
        assertEquals("a", argument.getString(0));
        assertEquals("b", argument.getString(1));
        assertEquals("a", result.getString(0));
        assertEquals("b", result.getString(1));
    }

    @Test
    public void onePrimitiveArgumentWithPrimitiveReturnTypeCall() {
        wrappedObject.onePrimitiveArgumentWithPrimitiveReturnType(5);
        JSONObject object = new JSONObject(writer.toString());
        JSONArray argument = object.getJSONArray("arguments");
        assertEquals(5, argument.get(0));
        int result = object.getInt("returnValue");
        assertEquals(6, result);
    }

    @Test
    public void throwExceptionMethodCall() throws Exception {
        try {
            wrappedObject.throwExceptionMethod();
        } catch (InvocationTargetException e) {
            assertEquals(e.getTargetException().getMessage(), "Test exception");
            JSONObject object = new JSONObject(writer.toString());
            assertTrue(object.has("method"));
            assertTrue(object.has("class"));
            assertTrue(object.has("arguments"));
            assertTrue(object.has("timestamp"));
            assertTrue(object.has("thrown"));
            assertEquals(object.keySet().size(), 5);
            assertEquals(object.get("thrown"), e.getTargetException().toString());
        }
    }

    @Test
    public void cyclicReturnTypeMethodCall() {
        List<Object> res = wrappedObject.cyclicReturnTypeMethod();
        JSONObject object = new JSONObject(writer.toString());
        JSONArray ret = object.getJSONArray("returnValue");
        assertEquals(res.get(0), res);
        assertEquals(res.get(1), "string");
        assertEquals(res.get(2), 2);
        assertEquals(res.get(3), res);
        assertEquals(ret.get(0), "cyclic");
        assertEquals(ret.get(1), "string");
        assertEquals(ret.get(2), 2);
        assertEquals(ret.get(3), "cyclic");
    }

    @Test
    public void testNonPrimitiveCyclicLinksTestCall() {
        List<Object> list = new Vector<>();
        List<Object> firstChildList = new Vector<>();
        List<Object> secondChildList = new Vector<>();
        firstChildList.add(0);
        firstChildList.add(list);
        secondChildList.add(firstChildList);
        secondChildList.add(1);
        list.add(firstChildList);
        list.add(secondChildList);
        wrappedObject.listArgumentMethod(list);
        JSONObject object = new JSONObject(writer.toString());
        JSONArray argument = object.getJSONArray("arguments").getJSONArray(0);
        assertEquals(argument.toString(), "[[0,\"cyclic\"],[[0,\"cyclic\"],1]]");
    }
}
