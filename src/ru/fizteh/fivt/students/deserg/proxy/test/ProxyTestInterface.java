package ru.fizteh.fivt.students.deserg.proxy.test;

import java.util.List;
import java.util.Set;

/**
 * Created by deserg on 10.12.14.
 */
public interface ProxyTestInterface {

    void methodWithNoArgs();

    void methodSingleArg(int arg);

    void methodMultipleArg(String arg1, String arg2);

    void methodIterableArg(List<String> list);

    float methodReturnsValue();

    Set<Object> methodReturnsIterable();

    void methodThrowsException();

}
