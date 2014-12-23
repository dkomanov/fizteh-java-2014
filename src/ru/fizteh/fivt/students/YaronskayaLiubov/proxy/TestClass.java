package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import java.io.IOException;
import java.util.List;

/**
 * Created by luba_yaronskaya on 04.12.14.
 */
public interface TestClass {
    void voidMethod();

    Integer intMethodIntInt(Integer x, Integer y);

    void exceptionMethod() throws IOException;

    List<Integer> listMethod();

    int[] arrayMethod();

    void voidMethodCyclicList(List<Object> list);
}
