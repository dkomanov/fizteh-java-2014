package ru.fizteh.fivt.students.lukina.proxy;

import java.io.IOException;
import java.util.ArrayList;

public class LoggingTestClass implements LoggingTestInterface {
    @Override
    public void takeIntReturnVoid(int i) {
        // nothing to do
    }

    @Override
    public int takeStringThrowException(String s) throws Exception {
        throw new IOException("Ooops");
    }

    @Override
    public void takeNothingReturnVoidThrowException() throws Exception {
        throw new RuntimeException("I love this test!");
    }

    @Override
    public ArrayList<?> takeIterableReturnArray(ArrayList<?> list) {
        ArrayList<?> res = new ArrayList<>(2);
        return res;
    }

    @Override
    public void takeNothingReturnNothing() {
        // nothing to return
    }

    @Override
    public int takeNothingReturnInt() {
        return 17;
    }
}
