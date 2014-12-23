package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy.test;

import java.util.List;
import java.util.Vector;

public class TestInterfaceImpl implements TestInterface {
    @Override
    public void noArgumentVoidMethod() {
    }

    @Override
    public void singleArgumentVoidMethod(Integer argument) {

    }

    @Override
    public void twoArgumentsVoidMethod(String first, Integer second) {

    }

    @Override
    public int noArgumentPrimitiveTypeMethod() {
        return 0;
    }

    @Override
    public void listArgumentMethod(List<Object> input) {

    }

    @Override
    public void arrayArgumentMethod(String[] args) {

    }

    @Override
    public List<String> listArgumentWithListReturnTypeMethod(List<String> input) {
        return input;
    }

    @Override
    public int onePrimitiveArgumentWithPrimitiveReturnType(int number) {
        return number + 1;
    }

    @Override
    public void throwExceptionMethod() throws Exception {
        throw new Exception("Test exception");
    }

    @Override
    public List<Object> cyclicReturnTypeMethod() {
        List<Object> result = new Vector<>();
        result.add(result);
        result.add("string");
        result.add(2);
        result.add(result);
        return result;
    }
}
