package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CLoggingProxyFactory;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.XMLParser;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Мирон on 30.11.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
public class ProxyTest {

    public static CLoggingProxyFactory factory;
    public StringWriter writer;
    public TestInterface obj;
    public XMLParser xmlParser;
    @BeforeClass
    public static void beforeClass() {
        factory = new CLoggingProxyFactory();
    }

    @Before
    public void before() {
        writer = new StringWriter();
        obj = (TestInterface)
                factory.wrap(writer, new TestInterfaceImplementation(), TestInterface.class);
        xmlParser = new XMLParser();
    }

    public boolean correctXML(String log) {
        try {
            xmlParser.parseString(log);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Test
    public void timestampTest() {
        obj.emptyArgumentMethod();
        String log = writer.toString();
        Assert.assertTrue(correctXML(log));
        Assert.assertTrue(log.split("timestamp").length > 1);
    }

    @Test
    public void classTest() {
        obj.emptyArgumentMethod();
        String log = writer.toString();
        Assert.assertTrue(correctXML(log));
        String[] logs = log.split("class=\"");
        Assert.assertTrue(logs.length >= 2);
        logs = logs[1].split("\"");
        Assert.assertEquals("ru.fizteh.fivt.students.LevkovMiron.ProxyTest.TestInterfaceImplementation", logs[0]);
    }

    @Test
    public void methodTest() {
        obj.emptyArgumentMethod();
        String log = writer.toString();
        String[] logs = log.split("name=\"");
        Assert.assertTrue(correctXML(log));
        Assert.assertTrue(logs.length >= 2);
        logs = logs[1].split("\"");
        Assert.assertEquals("emptyArgumentMethod", logs[0]);
    }

    @Test
    public void noArgumentsMethodTest() {
        obj.emptyArgumentMethod();
        String log = writer.toString();
        Assert.assertTrue(correctXML(log));
        String[] logs = log.split("arguments");
        Assert.assertEquals(logs.length, 2);
    }

    @Test
    public void nullArgumentMethodTest() {
        obj.integerArgumentMethod(null);
        String log = writer.toString();
        Assert.assertTrue(correctXML(log));
        String[] logs = log.split("<argument>");
        logs = logs[1].split("</argument>");
        Assert.assertEquals(logs[0], "<null/>");
    }

    @Test
    public void integerArgumentMethodTest() {
        Integer value = 2;
        obj.integerArgumentMethod(value);
        String log = writer.toString();
        Assert.assertTrue(correctXML(log));
        String[] logs = log.split("<argument>");
        logs = logs[1].split("</argument>");
        Assert.assertEquals(value.toString(), logs[0]);
    }

    @Test
    public void listArgumentMethodTest() {
        List<Object> list = new ArrayList<>();
        int sz = 3;
        Integer[] value = new Integer[sz];
        for (int i = 0; i < sz; i++) {
            value[i] = i;
            list.add(value[i]);
        }
        obj.listArgumentMethod(list);
        String log = writer.toString();
        String[] logs = log.split("arguments");
        Assert.assertEquals(3, logs.length);
        logs = logs[1].split("<argument>");
        logs = logs[1].split("</argument>");
        Assert.assertEquals(logs[0], "<ArrayList><value>0</value><value>1</value><value>2</value></ArrayList>");
    }
//
    @Test
    public void cyclicListArgumentMethodTest() {
        List<Object> firstList = new ArrayList<>();
        List<Object> secondList = new ArrayList<>();

        secondList.add(firstList);
        firstList.add(firstList);
        firstList.add(secondList);

        obj.listArgumentMethod(firstList);
        String log = writer.toString();
        String[] logs = log.split("<argument>");
        logs = logs[1].split("</argument>");
        Assert.assertEquals(3, logs[0].split("cyclic").length);
    }

    @Test
    public void twoArgumentsTest() {
        String str1 = "str1";
        String str2 = "str2";
        obj.stringArgumentsMethod(str1, str2);
        String log = writer.toString();
        String[] logs = log.split("<argument>");
        Assert.assertEquals(3, logs.length);
        Assert.assertEquals(str1, logs[1].split("</argument>")[0]);
        Assert.assertEquals(str2, logs[2].split("</argument>")[0]);
    }

    @Test(expected = IOException.class)
    public void exceptionTest() throws Throwable {
        try {
            obj.exceptionMethod();
        } catch (InvocationTargetException e) {
            String log = writer.toString();
            String[] logs = log.split("<thrown>");
            logs = logs[1].split("</thrown>");
            Assert.assertEquals("java.io.IOException: hi", logs[0]);
            throw e.getTargetException();
        }
    }

    @Test
    public void noExceptionTest() {
        obj.emptyArgumentMethod();
        String log = writer.toString();
        String[] logs = log.split("thrown");
        Assert.assertEquals(logs.length, 1);
    }

    @Test
    public void voidReturningMethodTest() {
        obj.voidMethod();
        String log = writer.toString();
        String[] logs = log.split("return");
        Assert.assertEquals(logs.length, 1);
    }

    @Test
    public void zeroReturningMethodTest() {
        obj.integerReturningMethod();
        String log = writer.toString();
        String[] logs = log.split("<return>");
        logs = logs[1].split("</return>");
        Assert.assertEquals("0", logs[0]);
    }

    @Test
    public void nullReturningMethodTest() {
        obj.nullReturningMethod();
        String log = writer.toString();
        String[] logs = log.split("<return>");
        logs = logs[1].split("</return>");
        Assert.assertEquals(logs[0], "<null/>");
    }

    @Test
    public void cyclicListReturningMethodTest() {
        obj.cyclicListReturningMethod();
        String log = writer.toString();
        String[] logs = log.split("<value>");
        for (int i = 0; i < logs.length; i++) {
            logs[i] = logs[i].split("</value>")[0];
        }
        Assert.assertEquals(logs[1], "<ArrayList>cyclic</ArrayList>");
        Assert.assertEquals(logs[2], "<ArrayList>cyclic</ArrayList>");
        Assert.assertEquals(logs[3], "42");
    }
}
