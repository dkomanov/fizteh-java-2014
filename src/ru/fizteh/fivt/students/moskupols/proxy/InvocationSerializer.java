package ru.fizteh.fivt.students.moskupols.proxy;

import java.lang.reflect.Method;

/**
 * Created by moskupols on 24.12.14.
 */
public interface InvocationSerializer {
    String serialize(Method method, Object[] args, Object returnValue);
}
