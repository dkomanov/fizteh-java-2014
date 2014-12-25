package ru.fizteh.fivt.students.moskupols.proxy;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by moskupols on 24.12.14.
 */
public class ProxyMain {
    public static void main(String[] args) throws IOException {
        final StringWriter writer = new StringWriter();
        SingleWriterLoggingProxyFactory proxyFactory = new LoggingProxyFactoryImpl(writer);
        final Object o = proxyFactory.wrap(
                new StringWriter(),
                Appendable.class);
        final Appendable w = (Appendable) o;
        try {
            w.append("abcde", 123, 22);
        } finally {
            System.out.println(writer.toString());
        }
    }
}
