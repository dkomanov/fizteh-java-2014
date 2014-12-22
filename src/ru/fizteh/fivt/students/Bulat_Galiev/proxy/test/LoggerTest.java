package ru.fizteh.fivt.students.Bulat_Galiev.proxy.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoggerTest implements LoggerTestInterface {
    @Override
    public void noArguments() {

    }

    @Override
    public void returnVoid(final int argument) {

    }

    @Override
    public final List<Object> listArgument(final List<Object> list) {
        List<Object> result = new ArrayList<>();
        result.addAll(list);
        list.add("list");
        return result;
    }

    @Override
    public final Integer manyArguments(final Integer a, final Integer b) {
        return a + b;
    }

    @Override
    public final void exception() throws IOException {
        throw new IOException("test exception");
    }

    @Override
    public void voidClass() {

    }

    @Override
    public final Object returnNull() {
        return null;
    }

}
