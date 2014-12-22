package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Мирон on 30.11.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
public class TestInterfaceImplementation implements TestInterface {

    @Override
    public void emptyArgumentMethod() { }

    @Override
    public void integerArgumentMethod(Integer a) { }

    @Override
    public void listArgumentMethod(List<Object> list) { }

    @Override
    public void stringArgumentsMethod(String str1, String str2) { }

    @Override
    public void exceptionMethod() throws Exception {
        throw new IOException("hi");
    }

    @Override
    public void voidMethod() { }

    @Override
    public Integer integerReturningMethod() {
        return 0;
    }

    @Override
    public Object nullReturningMethod() {
        return null;
    }

    @Override
    public List<Object> cyclicListReturningMethod() {
        List<Object> res = new ArrayList<>();
        res.add(res);
        res.add(res);
        res.add(42);
        return res;
    }
}
