package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.CLoggingProxyFactory;
import ru.fizteh.fivt.students.LevkovMiron.Proxy.LoggingProxyFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Created by Мирон on 30.11.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
public class ProxyTest {

    Writer writer;
    LoggingProxyFactory proxyFactory = new CLoggingProxyFactory();

    @Test()
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        writer = new OutputStreamWriter(System.out);
        Object proxy = proxyFactory.wrap(writer,
                new TestIntafaceImplementation(), TestInterface.class);
        TestInterface obj = (TestInterface) proxy;
        obj.zeroReturningMethod();
        writer.write("\n");
        obj.cyclicListReturningMethod();
        writer.write("\n");
        obj.integerArgumentMethod(1);
        writer.write("\n");
        obj.iOExceptionHiMethod();
        writer.write("\n");
        obj.listArgumentMethod(Arrays.asList(1, 2));
        writer.write("\n");
        obj.noArgumentsMethod();
        writer.write("\n");
        obj.nullReturningMethod();
        writer.write("\n");
        obj.twoStringArgumentMethod("a", "b");
        writer.write("\n");
        obj.voidReturningMethod();
        writer.write("\n");
        writer.flush();
    }

}
