package ru.fizteh.fivt.students.moskupols.proxy;

/**
 * Created by moskupols on 25.12.14.
 */
public interface SingleWriterLoggingProxyFactory {
    Object wrap(Object implementation, Class<?> interfaceClass);
}
