package ru.fizteh.fivt.students.torunova.proxy.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.torunova.proxy.ProxyFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LoggingProxyFactoryTest {
    LoggingProxyFactory factory = new ProxyFactory();
    TestInterface proxy;
    StringWriter writer;
    @Before
    public void setUp() {
        writer = new StringWriter();
        proxy = (TestInterface) factory.wrap(writer, new TestInterfaceImpl(), TestInterface.class);
    }
    @Test
    public void testReturnsPrimitiveType() {
        proxy.methodReturnsBool(false);
        String buffer = writer.getBuffer().toString();
        //System.out.println(buffer);
        assertTrue(buffer.matches(".*name=\"methodReturnsBool\".*false.*false.*"));
        proxy.methodReturnsInt(42);
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsInt\".*42.*42.*"));
        proxy.methodReturnsLong(42L);
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsLong\".*42.*42.*"));
        proxy.methodReturnsFloat(42.42f);
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsFloat\".*42\\.42.*42\\.42.*"));
        proxy.methodReturnsDouble(42.42);
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsDouble\".*42\\.42.*42\\.42.*"));
        proxy.methodReturnsShort((short) 42);
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsShort\".*42.*42.*"));
        proxy.methodReturnsChar('c');
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsChar\".*c.*c.*"));
        proxy.methodReturnsByte((byte) 42);
        buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*name=\"methodReturnsByte\".*42.*42.*"));
    }
    @Test
    public void testThrowsException() {
        TestException e = new TestException();
        try {
            proxy.methodWithException(e);
        } catch (TestException e1) {
            //ignored
        }
        String buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*<thrown>ru.fizteh.fivt.students.torunova.proxy.tests.TestException</thrown>.*"));
    }
    @Test
    public void testReturnsVoid() {
        proxy.methodReturnsVoid();
        String buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*<arguments/>.*<return/>.*"));
    }
    @Test
    public void testReturnsNotCyclicIterable() {
        List<Object> list = new ArrayList<>();
        list.add("Some string");
        list.add(42);
        proxy.methodReturnsIterable(list);
        String buffer = writer.getBuffer().toString();
        assertTrue(buffer.matches(".*<list><value>Some string</value><value>42</value></list>.*"));
    }
    @Test
    public void testReturnsCyclicIterable() {
        List<Object> list = new ArrayList<>();
        list.add("Some string");
        list.add(42);
        list.add(list);
        proxy.methodReturnsIterable(list);
        String buffer = writer.getBuffer().toString();
        System.out.println(buffer);
        assertTrue(buffer.matches(".*<value>cyclic</value>.*"));

    }

}
