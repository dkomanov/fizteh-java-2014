package ru.fizteh.fivt.students.VasilevKirill.proxy.tests;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kirill on 30.11.2014.
 */
public class TestingClass implements TestingInterface {
    @Override
    public void noArgumentsMethod() {

    }

    @Override
    public void oneArgumentMethod(String a) {

    }

    @Override
    public void twoArgumentMethod(Integer a, Integer b) {

    }

    @Override
    public void listArgumentsMethod(List<String> list) {

    }

    @Override
    public void cyclicArgumentsMethod(List<Object> list) {

    }

    @Override
    public Integer smthReturningMethod() {
        return 0;
    }

    @Override
    public Object nullReturningMethod() {
        return null;
    }

    @Override
    public Integer exceptionMethod() throws IOException {
        throw new IOException("Example of exception");
    }
}
