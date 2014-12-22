package ru.fizteh.fivt.students.gudkov394.Proxy.test;

import java.util.List;

/**
 * Created by kagudkov on 02.12.14.
 */
public interface TestInterface {

    void emptyArgumentMethod();

    void integerArgumentMethod(Integer a);

    void listArgumentMethod(List<Object> list);

    void stringArgumentsMethod(String str1, String str2);

    void exceptionMethod() throws Exception;

    void voidMethod();

    Integer integerReturningMethod();

    Object nullReturningMethod();

    List<Object> cyclicListReturningMethod();
}


