package ru.fizteh.fivt.students.dsalnikov.tests;

import java.util.List;

public interface ProxyInterface {
    String methodWithoutArgs();

    String methodMixedArgs(String string, Integer[] intArray, List<String> list);

    List<Object> methodWithCycleReferences(List<Object> list);

    void methodThrowsException(String string, Integer integer, List<String> list) throws IllegalStateException;
}
