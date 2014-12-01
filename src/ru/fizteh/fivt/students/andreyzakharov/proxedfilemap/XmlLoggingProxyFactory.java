package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;

public class XmlLoggingProxyFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return null;
    }
}
