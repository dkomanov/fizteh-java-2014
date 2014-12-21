package ru.fizteh.fivt.students.standy66_new.runners;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.standy66_new.proxy.JSONLoggingProxyFactory;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class Task7Runner {
    //It is not full scale tests, that one can be found in /tests/task7/JSONLoggingProxyFactoryTest.java
    public static void main(String[] args) throws Throwable {
        JSONLoggingProxyFactory factory = new JSONLoggingProxyFactory();
        HashMap map = new HashMap();
        List<Object> list = new ArrayList<>();
        List<Object> nested = new ArrayList<>();
        nested.add("str");
        nested.add(false);
        nested.add(3.14f);
        nested.add(Long.MAX_VALUE);
        nested.add(list);
        list.add(nested);
        list.add(list);


        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        Map proxy = (Map) factory.wrap(bw, map, Map.class);

        proxy.put("a", "b");
        proxy.put("a", "c");
        proxy.put("a", list);

        LoggingProxyFactory fac1 = (LoggingProxyFactory) factory.wrap(bw, new LoggingProxyFactory() {
            @Override
            public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
                throw new RuntimeException("azaza");
            }
        }, LoggingProxyFactory.class);
        try {
            fac1.wrap(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.flush();

    }
}
