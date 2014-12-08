package ru.fizteh.fivt.students.standy66_new.tests.task7;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.standy66_new.proxy.JSONLoggingProxyFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author andrew
 *         Created by andrew on 08.12.14.
 */
@RunWith(Parameterized.class)
public class JSONLoggingProxyFactoryTest {
    private Class<? extends LoggingProxyFactory> factoryClass;
    private StringWriter stringWriter;
    private TestedInterface proxy;

    public JSONLoggingProxyFactoryTest(Class<? extends LoggingProxyFactory> factoryClass) {
        this.factoryClass = factoryClass;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {JSONLoggingProxyFactory.class}
        });
    }

    @Before
    public void setUp() throws Exception {
        stringWriter = new StringWriter();
        LoggingProxyFactory factory = factoryClass.newInstance();
        proxy = (TestedInterface) factory.wrap(stringWriter, new Impl(), TestedInterface.class);
    }

    @Test
    public void testPrimitiveTypes() throws Exception {
        proxy.identity(1);
        assertEquals(1, retVal());
        proxy.identity((byte) 1);
        assertEquals(1, retVal());
        proxy.identity((short) 1);
        assertEquals(1, retVal());
        proxy.identity((long) 1);
        assertEquals(1, retVal());
        proxy.identity(3.14);
        assertEquals(3.14, retVal());
        proxy.identity(3.14f);
        assertEquals(3.14, retVal());
        proxy.identity(false);
        assertEquals(false, retVal());
        proxy.identity('a');
        assertEquals("a", retVal());
    }

    @Test(expected = JSONException.class)
    public void testReturnsVoid() throws Exception {
        proxy.returnsVoid();
        assertNull(retVal());
    }

    @Test(expected = JSONException.class)
    public void testNullArgs() throws Exception {
        proxy.returnsVoid();
        assertNull(getLog().get("arguments"));
    }

    @Test
    public void testCyclicArgs() throws Exception {
        List<Object> l = new ArrayList<>();
        l.add(l);
        proxy.identity(l);
        JSONArray ret = (JSONArray) retVal();
        JSONArray inner = (JSONArray) ret.get(0);
        assertEquals("cyclic", inner.getString(0));
    }

    @Test
    public void testNestedArgs() throws Exception {
        List<Object> l = new ArrayList<>();
        List<Object> nested1 = new ArrayList<>();
        List<Object> nested2 = new ArrayList<>();
        l.add(nested1);
        l.add(nested2);
        nested1.add(1);
        nested1.add(2);
        nested1.add(3);
        nested2.add("one");
        nested2.add("two");
        nested2.add("three");
        proxy.identity(l);

        JSONArray a = (JSONArray) retVal();
        JSONArray n1 = a.getJSONArray(0);
        JSONArray n2 = a.getJSONArray(1);
        assertEquals(1, n1.getInt(0));
        assertEquals(2, n1.getInt(1));
        assertEquals(3, n1.getInt(2));

        assertEquals("one", n2.getString(0));
        assertEquals("two", n2.getString(1));
        assertEquals("three", n2.getString(2));
    }

    @Test
    public void testThrows() throws Throwable {
        Class c = Class.forName("java.lang.RuntimeException");
        proxy = getThrowSuppressor(TestedInterface.class, proxy);

        proxy.throwsThrowable(new RuntimeException(""));
        assertSame(RuntimeException.class, thrown());

        proxy.throwsThrowable(new IOException(""));
        assertSame(IOException.class, thrown());

        proxy.throwsThrowable(new StringIndexOutOfBoundsException(""));
        assertSame(StringIndexOutOfBoundsException.class, thrown());
    }

    private JSONObject getLog() {
        String logString = stringWriter.getBuffer().toString();
        stringWriter.getBuffer().setLength(0);
        return new JSONObject(logString);
    }

    private Object retVal() {
        return getLog().get("returnValue");
    }

    private Class<?> thrown() throws ClassNotFoundException {
        return Class.forName((String) getLog().get("thrown"));
    }

    private <T> T getThrowSuppressor(Class<? extends T> cls, T impl) {
        return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls},
                (proxy, method, args) ->
                {
                    try {
                        return method.invoke(impl, args);
                    } catch (Throwable t) {
                        return null;
                    }
                }));
    }

    public interface TestedInterface {
        void returnsVoid();

        void throwsThrowable(Throwable t) throws Throwable;

        <T> T identity(T t);

        int identity(int value);

        byte identity(byte value);

        short identity(short value);

        long identity(long value);

        float identity(float value);

        double identity(double value);

        boolean identity(boolean value);

        char identity(char value);
    }

    private static class Impl implements TestedInterface {
        @Override
        public void returnsVoid() {
        }

        @Override
        public void throwsThrowable(Throwable t) throws Throwable {
            throw t;
        }

        @Override
        public <T> T identity(T t) {
            return t;
        }

        @Override
        public int identity(int value) {
            return value;
        }

        @Override
        public byte identity(byte value) {
            return value;
        }

        @Override
        public short identity(short value) {
            return value;
        }

        @Override
        public long identity(long value) {
            return value;
        }

        @Override
        public float identity(float value) {
            return value;
        }

        @Override
        public double identity(double value) {
            return value;
        }

        @Override
        public boolean identity(boolean value) {
            return value;
        }

        @Override
        public char identity(char value) {
            return value;
        }
    }
}
