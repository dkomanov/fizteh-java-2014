package ru.fizteh.fivt.students.deserg.proxy.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Set<Object> methodReturnsIterable() {

        Set<Object> set = new HashSet<>();
        Set<String> anotherSet = new HashSet<>();

        set.add(1.5);
        set.add("123321");
        set.add(anotherSet);
        set.add(set);

        anotherSet.add("key1");
        anotherSet.add("key2");

        return set;
    }

    @Override
    public void methodThrowsException() {
        throw new IllegalArgumentException("ProxyTestImplementation: methodThrowsException: thrown");
    }


}
