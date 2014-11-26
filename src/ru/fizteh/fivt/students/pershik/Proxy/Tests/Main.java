package ru.fizteh.fivt.students.pershik.Proxy.Tests;

import ru.fizteh.fivt.students.pershik.Proxy.MyLoggingProxyFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by pershik on 11/26/14.
 */
public class Main {
    static TestingClass testingClass;

    public static void main(String[] args) throws IOException {
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        TestingInterface wrap = (TestingInterface)
                factory.wrap(new StringWriter(), new TestingClass(), TestingInterface.class);
        try {
            wrap.firstMethod();
            wrap.secondMethod();
            wrap.exceptionMethod();
        } catch (Exception e) {
            // nothing to do
        }
        //wrap.thirdMethod();
    }
}
