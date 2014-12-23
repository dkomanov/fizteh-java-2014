package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akhtyamovpavel on 30.11.14.
 */
public class LogTestClass implements LogTestInterface {
    @Override
    public void noArgumentsMethod() {

    }

    @Override
    public void numericArgumentMethod(Integer number) {

    }

    @Override
    public void listArgumentMethod(List<Object> list) {

    }

    @Override
    public Integer manyArgumentsMethod(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public void exceptionMethod() throws IOException {
        throw new IOException("test exception");
    }

    @Override
    public void voidClassReturningMethod() {

    }

    @Override
    public Object nullReturningMethod() {
        return null;
    }

    @Override
    public Object cyclicMethod() {
        ArrayList<Object> list = new ArrayList<>();
        list.add(list);
        list.add(4);
        return list;
    }
}
