package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.logs.MyLoggingProxyFactory;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyLoggingProxyFactoryTest {
    public interface TestInterface {
        void simpleMethod();
        int incMethod(int value);
        void exceptionMethod() throws Exception;
        int addMethod(int[] values);
        List<String> listMethod(List<String> list);
        List<String> nullMethod(List<String> list);
        void severalArgsMethod(int[] values, String str);
        void listsMethod(List<Object> lists);
    }

    public class TestImplementation implements TestInterface {
        public void simpleMethod() {
        }

        public int incMethod(int value) {
            return value + 1;
        }

        public void exceptionMethod() throws Exception {
            throw new Exception("reason");
        }

        public int addMethod(int[] values) {
            int sum = 0;
            for (int value: values) {
                sum += value;
            }
            return sum;
        }

        public List<String> listMethod(List<String> list) {
            return list;
        }

        public List<String> nullMethod(List<String> list) {
            return list;
        }

        public void severalArgsMethod(int[] values, String str) {
        }

        public void listsMethod(List<Object> lists) {
        }
    }

    TestImplementation implementation = new TestImplementation();
    MyLoggingProxyFactory loggingProxyFactory = new MyLoggingProxyFactory();
    StringWriter writer;

    @Before
    public void setUp() throws Exception {
        writer = new StringWriter();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimple() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        long timestampBefore = System.currentTimeMillis();
        proxyImplementation.simpleMethod();
        long timestampAfter = System.currentTimeMillis();
        JSONObject jsonLog = new JSONObject(writer.toString());
        assertTrue(jsonLog.getLong("timestamp") >= timestampBefore && jsonLog.getLong("timestamp") <= timestampAfter);
        assertEquals(jsonLog.getString("class"), implementation.getClass().getName());
        assertEquals(jsonLog.getString("method"), "simpleMethod");
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertTrue(args != null);
        assertTrue(args.length() == 0);
    }

    @Test
    public void testWithInt() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        proxyImplementation.incMethod(5);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals(5, args.get(0));
        assertEquals(6, jsonLog.getInt("returnValue"));
    }

    @Test
    public void testWithException() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        try {
            proxyImplementation.exceptionMethod();
        } catch (InvocationTargetException e) {
            throw new Exception("Exception should not be wrapped");
        } catch (Exception e) {
            JSONObject jsonLog = new JSONObject(writer.toString());
            assertEquals("java.lang.Exception: reason", jsonLog.getString("thrown"));
            return;
        }
        throw new Exception("Test should not execute this string");
    }

    @Test
    public void testWithArray() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        int[] values = {1, 2, 3, 4, 5};
        proxyImplementation.addMethod(values);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals("[1,2,3,4,5]", args.get(0).toString());
        assertEquals(15, jsonLog.getInt("returnValue"));
    }

    @Test
    public void testWithList() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        List<String> list = new ArrayList<>();
        list.add("qwer");
        list.add("asdf");
        list.add("zxcv");
        proxyImplementation.listMethod(list);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals("[\"qwer\",\"asdf\",\"zxcv\"]", args.get(0).toString());
        assertEquals("[\"qwer\",\"asdf\",\"zxcv\"]", jsonLog.get("returnValue").toString());
    }

    @Test
    public void testWithNull() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        proxyImplementation.nullMethod(null);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals("null", args.get(0).toString());
        assertEquals("null", jsonLog.get("returnValue").toString());
    }

    @Test
    public void testWithSeveralArgs() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        int[] values = {1, 2, 3, 4, 5};
        String str = "test string";
        proxyImplementation.severalArgsMethod(values, str);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals("[1,2,3,4,5]", args.get(0).toString());
        assertEquals(str, args.get(1).toString());
    }

    @Test
    public void testWithListInList() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        List<Object> mainList = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list2.add(3);
        list2.add(4);
        mainList.add(list1);
        mainList.add(list2);
        proxyImplementation.listsMethod(mainList);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals("[[1,2],[3,4]]", args.get(0).toString());
    }

    @Test
    public void testWithCyclicLists() throws Exception {
        TestInterface proxyImplementation =
                (TestInterface) loggingProxyFactory.wrap(writer, implementation, TestInterface.class);
        List<Object> mainList = new ArrayList<>();
        List<Object> otherList = new ArrayList<>();
        otherList.add(1);
        otherList.add(2);
        otherList.add(mainList);
        mainList.add(otherList);
        mainList.add(3);
        proxyImplementation.listsMethod(mainList);
        JSONObject jsonLog = new JSONObject(writer.toString());
        JSONArray args = jsonLog.getJSONArray("arguments");
        assertEquals("[[1,2,\"cyclic\"],3]", args.get(0).toString());
    }
}
