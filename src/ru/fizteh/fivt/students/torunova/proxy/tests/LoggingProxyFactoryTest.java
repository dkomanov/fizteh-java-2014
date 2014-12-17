package ru.fizteh.fivt.students.torunova.proxy.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.torunova.proxy.ProxyFactory;

import java.io.StringWriter;

public class LoggingProxyFactoryTest {
    LoggingProxyFactory factory = new ProxyFactory();
    TestInterfaceImpl proxy;
    StringWriter writer;
    @Before
    public void setUp() {
        writer = new StringWriter();
        proxy = (TestInterfaceImpl) factory.wrap(writer, TestInterfaceImpl.class, TestInterface.class);
    }
    @Test
    public void testReturnsPrimitiveType() {
        proxy.methodReturnsBool(false);
    }

}
