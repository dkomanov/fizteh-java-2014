package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.test;

import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public interface TestInterface {
    void noArgumentVoidMethod();
    void singleArgumentVoidMethod(Integer argument);
    void twoArgumentsVoidMethod(String first, Integer second);
    int noArgumentPrimitiveTypeMethod();
    void listArgumentMethod(List<Object> input);
    void arrayArgumentMethod(String[] args);
    List<String> listArgumentWithListReturnTypeMethod(List<String> input);
    int onePrimitiveArgumentWithPrimitiveReturnType(int number);
    void throwExceptionMethod() throws Exception;
    List<Object> cyclicReturnTypeMethod();
}
