package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luba_yaronskaya on 04.12.14.
 */
public final class LoggingTestClass implements TestClass {
    @Override
    public void voidMethod() {

    }

    @Override
    public Integer intMethodIntInt(Integer x, Integer y) {
        return 1;
    }

    @Override
    public void exceptionMethod() throws IOException {
        throw new IOException();
    }

    @Override
    public List<Integer> listMethod() {
        Integer[] array = {1, 2};
        return new ArrayList<>(Arrays.asList(array));
    }

    @Override
    public int[] arrayMethod() {
        int[] array = {1, 2};
        return array;
    }

    @Override
    public void voidMethodCyclicList(List<Object> list) {

    }
}
