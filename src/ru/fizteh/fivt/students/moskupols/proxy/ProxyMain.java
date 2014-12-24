package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moskupols on 24.12.14.
 */
public class ProxyMain {
    public static void main(String[] args) {
        LoggingProxyFactory proxyFactory = new LoggingProxyFactoryImpl();
        final StringWriter writer = new StringWriter();
        final Object o = proxyFactory.wrap(
                writer,
                new HashMap<String, String>(),
                Map.class);
        final Map<String, String> stringMap = (Map<String, String>) o;
        System.out.println(stringMap.put("sdf", "tt"));
        System.out.println(stringMap.get("sdf"));
        System.out.println(stringMap.get("ss"));
        System.out.println(writer.toString());
    }
}
