package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.dsalnikov.proxy.ProxyLoggingFactoryImpl;
import ru.fizteh.fivt.students.dsalnikov.utils.StringUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ProxyTest {
    ProxyLoggingFactoryImpl testProxyLoggingFactory;
    StringWriter writer;
    ProxyInterfaceImpl impl;

    @Before
    public void setUp() {
        writer = new StringWriter();
        testProxyLoggingFactory = new ProxyLoggingFactoryImpl();
        impl = new ProxyInterfaceImpl();
    }

    @Test
    public void methodWithoutArgsTest() {
        ProxyInterface testProxy
                = (ProxyInterface) testProxyLoggingFactory.wrap(writer, impl, ProxyInterface.class);
        testProxy.methodWithoutArgs();
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.dsalnikov.tests.ProxyInterfaceImpl\" "
                        + "name=\"methodWithoutArgs\"><arguments></arguments>"
                        + "<return>methodWithoutArgs result</return></invoke>",
                StringUtils.cutTimeStamp(writer.toString()));
    }

    @Test
    public void methodMixedArgsTest() {
        ProxyInterface testProxy
                = (ProxyInterface) testProxyLoggingFactory.wrap(writer, impl, ProxyInterface.class);
        Integer[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("0");
        testProxy.methodMixedArgs("Hello World!", data, list);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.dsalnikov.tests.ProxyInterfaceImpl\" "
                        + "name=\"methodMixedArgs\"><arguments><argument>Hello World!</argument>"
                        + "<argument>" + data.toString() + "</argument><argument><list><value>1</value><value>2"
                        + "</value>"
                        + "<value>3</value><value>4</value><value>5</value><value>6</value><value>7</value><value>8"
                        + "</value>"
                        + "<value>9</value><value>0</value></list></argument></arguments>"
                        + "<return>methodMixedArgs result</return></invoke>",
                StringUtils.cutTimeStamp(writer.toString()));
    }

    @Test
    public void methodWithCycleReferences() {
        ProxyInterface testProxy
                = (ProxyInterface) testProxyLoggingFactory.wrap(writer, impl, ProxyInterface.class);
        List<Object> list = new ArrayList<>();
        list.add(null);
        list.add(list);
        testProxy.methodWithCycleReferences(list);
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.dsalnikov.tests.ProxyInterfaceImpl\" "
                        + "name=\"methodWithCycleReferences\"><arguments><argument><list><value><null></null>"
                        + "</value><value>"
                        + "<list><value><null></null></value>"
                        + "<value>cyclic</value></list></value></list></argument></arguments>"
                        + "<return>[null, (this Collection)]</return></invoke>",
                StringUtils.cutTimeStamp(writer.toString()));
    }

    @Test
    public void methodThrowsException() {
        ProxyInterface testProxy
                = (ProxyInterface) testProxyLoggingFactory.wrap(writer, impl, ProxyInterface.class);
        Integer data = 1;
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("0");
        try {
            testProxy.methodThrowsException("Hello World!", data, list);
        } catch (Exception ignored) {
            // exception is ok!
        }
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.dsalnikov.tests.ProxyInterfaceImpl\" "
                        + "name=\"methodThrowsException\"><arguments><argument>Hello World!</argument><argument>"
                        + "1</argument>"
                        + "<argument><list><value>1</value><value>2</value><value>3</value><value>4</value><value>"
                        + "5</value>"
                        + "<value>6</value><value>7</value><value>8</value><value>9</value><value>0</value>"
                        + "</list></argument>"
                        + "</arguments><thrown>java.lang."
                        + "IllegalStateException: implementation method throws exception: ok!</thrown></invoke>",
                StringUtils.cutTimeStamp(writer.toString()));
    }
}
