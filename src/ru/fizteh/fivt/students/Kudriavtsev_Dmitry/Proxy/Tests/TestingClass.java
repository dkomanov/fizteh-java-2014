package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ВАНЯ on 20.12.2014.
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
        throw new IOException("Hello World!");
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
        res.add(42);
        return res;
    }
}
