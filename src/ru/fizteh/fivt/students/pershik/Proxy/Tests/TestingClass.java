package ru.fizteh.fivt.students.pershik.Proxy.Tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pershik on 11/26/14.
 */
public class TestingClass implements TestingInterface {
    @Override
    public void noArgumentsMethod() { }

    @Override
    public void integerArgumentMethod(Integer a) { }

    @Override
    public void listArgumentMethod(List<Object> list) { }

    @Override
    public void twoStringArgumentMethod(String str1, String str2) { }

    @Override
    public void iOExceptionHiMethod() throws IOException {
        throw new IOException("hi");
    }

    @Override
    public void voidReturningMethod() { }

    @Override
    public Integer zeroReturningMethod() {
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
        res.add(56);
        return res;
    }
}
