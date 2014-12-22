package ru.fizteh.fivt.students.dsalnikov.tests;

import java.util.List;

public class ProxyInterfaceImpl implements ProxyInterface {
    @Override
    public String methodWithoutArgs() {
        return "methodWithoutArgs result";
    }

    @Override
    public String methodMixedArgs(String string, Integer[] intArray, List<String> list) {
        return "methodMixedArgs result";
    }

    @Override
    public List<Object> methodWithCycleReferences(List<Object> list) {
        return list;
    }

    @Override
    public void methodThrowsException(String string, Integer integer, List<String> list)
            throws IllegalStateException {
        throw new IllegalStateException("implementation method throws exception: ok!");
    }
}
