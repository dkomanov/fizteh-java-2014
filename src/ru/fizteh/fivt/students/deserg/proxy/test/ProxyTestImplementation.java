package ru.fizteh.fivt.students.deserg.proxy.test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by deserg on 10.12.14.
 */
public class ProxyTestImplementation implements ProxyTestInterface {

    @Override
    public void methodWithNoArgs() {}

    @Override
    public void methodSingleArg(int arg) {}

    @Override
    public void methodMultipleArg(String arg1, String arg2) {}

    @Override
    public void methodIterableArg(List<String> list) {}

    @Override
    public float methodReturnsValue() {
        return new Float(8.124134);
    }

    @Override
    public List<Object> methodReturnsIterable() {

        List<Object> list = new LinkedList<>();

        list.add(1.5);
        list.add("123321");

        return list;
    }

    @Override
    public void methodThrowsException() {
        throw new IllegalArgumentException("ProxyTestImplementation: methodThrowsException: thrown");
    }

    @Override
    public List<Object> methodCyclicReturn() {

        List<Object> list = new LinkedList<>();
        list.add("Str1");
        list.add(list);

        return list;

    }


}
