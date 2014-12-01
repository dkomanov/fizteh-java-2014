package ru.fizteh.fivt.students.gudkov394.Proxy.test;

import java.util.List;

/**
 * Created by kagudkov on 02.12.14.
 */
public interface testInterface {
    void noArgumentsMethod();

    void integerArgumentMethod(Integer a);

    void listArgumentMethod(List<Object> list);

    void twoStringArgumentMethod(String str1, String str2);

    void iOExceptionHiMethod() throws Exception;

    void voidReturningMethod();

    Integer zeroReturningMethod();

    Object nullReturningMethod();

    List<Object> cyclicListReturningMethod();
}


