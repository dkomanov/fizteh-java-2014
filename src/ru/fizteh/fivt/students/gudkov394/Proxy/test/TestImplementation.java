package ru.fizteh.fivt.students.gudkov394.Proxy.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kagudkov on 02.12.14.
 */
public class TestImplementation implements TestInterface {


    @Override
    public void emptyArgumentMethod() {

    }

    @Override
    public void integerArgumentMethod(Integer a) {

    }

    @Override
    public void listArgumentMethod(List<Object> list) {

    }

    @Override
    public void stringArgumentsMethod(String str1, String str2) {

    }

    @Override
    public void exceptionMethod() throws Exception {
        throw new IOException();

    }

    @Override
    public void voidMethod() {

    }

    @Override
    public Integer integerReturningMethod() {
        return null;
    }

    @Override
    public Object nullReturningMethod() {
        return null;
    }

    @Override
    public List<Object> cyclicListReturningMethod() {
        List<Object> res = new ArrayList<>();
        res.add(42);
        res.add(res);
        res.add(res);
        return res;
    }
}
