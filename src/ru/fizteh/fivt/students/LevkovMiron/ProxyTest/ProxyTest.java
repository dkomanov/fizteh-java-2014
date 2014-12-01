package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import org.junit.Before;
import org.junit.BeforeClass;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CLoggingProxyFactory;

import java.io.StringWriter;

/**
 * Created by Мирон on 30.11.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
public class ProxyTest {

    public static CLoggingProxyFactory factory;
    public StringWriter writer;
    public TestInterface obj;

    @BeforeClass
    public static void beforeClass() {
        factory = new CLoggingProxyFactory();
    }

    @Before
    public void before() {
        writer = new StringWriter();
        obj = (TestInterface)
                factory.wrap(writer, new TestIntafaceImplementation(), TestInterface.class);
    }

//    @Test
//    public void timestampTest() {
//        obj.noArgumentsMethod();
//        String log = writer.toString();
//        Assert.assertTrue(jsonObject.has("timestamp"));
//    }
//
//    @Test
//    public void classTest() {
//        obj.noArgumentsMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        Assert.assertEquals("ru.fizteh.fivt.students.pershik.Proxy.Tests.TestingClass",
//                jsonObject.get("class"));
//    }
//
//    @Test
//    public void methodTest() {
//        obj.noArgumentsMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        Assert.assertEquals("noArgumentsMethod", jsonObject.get("method"));
//    }
//
//    @Test
//    public void noArgumentsMethodTest() {
//        obj.noArgumentsMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray array = jsonObject.getJSONArray("arguments");
//        Assert.assertEquals(0, array.length());
//    }
//
//    @Test
//    public void nullArgumentMethodTest() {
//        obj.integerArgumentMethod(null);
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray array = jsonObject.getJSONArray("arguments");
//        Assert.assertEquals(1, array.length());
//        Assert.assertNotNull(array.get(0));
//    }
//
//    @Test
//    public void integerArgumentMethodTest() {
//        Integer value = 2;
//        obj.integerArgumentMethod(value);
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray array = jsonObject.getJSONArray("arguments");
//        Assert.assertEquals(1, array.length());
//        Assert.assertEquals(value, array.get(0));
//    }
//
//    @Test
//    public void listArgumentMethodTest() {
//        List<Object> list = new ArrayList<>();
//        int sz = 3;
//        Integer[] value = new Integer[sz];
//        for (int i = 0; i < sz; i++) {
//            value[i] = i;
//            list.add(value[i]);
//        }
//        obj.listArgumentMethod(list);
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray array = jsonObject.getJSONArray("arguments");
//        Assert.assertEquals(1, array.length());
//        JSONArray arg = array.getJSONArray(0);
//        Assert.assertEquals(sz, arg.length());
//        for (int i = 0; i < sz; i++) {
//            Assert.assertEquals(value[i], arg.get(i));
//        }
//    }
//
//    @Test
//    public void cyclicListArgumentMethodTest() {
//        List<Object> firstList = new ArrayList<>();
//        List<Object> secondList = new ArrayList<>();
//
//        secondList.add(firstList);
//        firstList.add(firstList);
//        firstList.add(secondList);
//
//        obj.listArgumentMethod(firstList);
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray array = jsonObject.getJSONArray("arguments");
//        Assert.assertEquals(1, array.length());
//
//        JSONArray firstArray = array.getJSONArray(0);
//        Assert.assertEquals(2, firstArray.length());
//        Assert.assertEquals("cyclic", firstArray.get(0));
//        JSONArray secondArray = firstArray.getJSONArray(1);
//        Assert.assertEquals(1, secondArray.length());
//        Assert.assertEquals("cyclic", secondArray.get(0));
//    }
//
//    @Test
//    public void twoArgumentsTest() {
//        String str1 = "str1";
//        String str2 = "str2";
//        obj.twoStringArgumentMethod(str1, str2);
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray array = jsonObject.getJSONArray("arguments");
//        Assert.assertEquals(2, array.length());
//        Assert.assertEquals(str1, array.get(0));
//        Assert.assertEquals(str2, array.get(1));
//    }
//
//    @Test(expected = IOException.class)
//    public void exceptionTest() throws IOException {
//        try {
//            obj.iOExceptionHiMethod();
//        } catch (Exception e) {
//            String log = writer.toString();
//            JSONObject jsonObject = new JSONObject(log);
//            String exceptionStr = jsonObject.getString("thrown");
//            Assert.assertEquals("java.io.IOException: hi", exceptionStr);
//            throw e;
//        }
//    }
//
//    @Test
//    public void noExceptionTest() {
//        obj.noArgumentsMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        Assert.assertFalse(jsonObject.has("thrown"));
//    }
//
//    @Test
//    public void voidReturningMethodTest() {
//        obj.voidReturningMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        Assert.assertFalse(jsonObject.has("returnValue"));
//    }
//
//    @Test
//    public void zeroReturningMethodTest() {
//        obj.zeroReturningMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        Integer res = jsonObject.getInt("returnValue");
//        Assert.assertEquals((Integer) 0, res);
//    }
//
//    @Test
//    public void nullReturningMethodTest() {
//        obj.nullReturningMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        Object res = jsonObject.get("returnValue");
//        Assert.assertEquals(res, null);
//    }
//
//    @Test
//    public void cyclicListReturningMethodTest() {
//        obj.cyclicListReturningMethod();
//        String log = writer.toString();
//        JSONObject jsonObject = new JSONObject(log);
//        JSONArray res = (JSONArray) jsonObject.get("returnValue");
//        Assert.assertEquals("cyclic", res.get(0));
//        Assert.assertEquals("cyclic", res.get(1));
//        Assert.assertEquals(56, res.get(2));
//        Assert.assertEquals(3, res.length());
//    }
}
