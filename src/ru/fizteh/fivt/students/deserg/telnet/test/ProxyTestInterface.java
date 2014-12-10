package ru.fizteh.fivt.students.deserg.telnet.test;

import java.util.List;

/**
 * Created by deserg on 10.12.14.
 */
public interface ProxyTestInterface {

    void methodWithNoArgs();

    void methodSingleArg(int arg);

    void methodMultipleArg(String arg1, String arg2);

    void methodIterableArg(List<String> list);

    float methodReturnsValue();

    List<Object> methodReturnsIterable();

    void methodThrowsException();

    List<Object> methodCyclicReturn();

}
