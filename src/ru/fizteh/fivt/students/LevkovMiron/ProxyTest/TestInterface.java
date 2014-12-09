package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import java.util.List;

/**
 * Created by Мирон on 30.11.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
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
