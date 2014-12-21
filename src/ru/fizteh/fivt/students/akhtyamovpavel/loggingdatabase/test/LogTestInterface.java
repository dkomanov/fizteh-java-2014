package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.test;

import java.util.List;

/**
 * Created by akhtyamovpavel on 30.11.14.
 */
public interface LogTestInterface {

    void noArgumentsMethod();

    void numericArgumentMethod(Integer number);

    void listArgumentMethod(List<Object> list);

    Integer manyArgumentsMethod(Integer a, Integer b);

    void exceptionMethod() throws Exception;

    void voidClassReturningMethod();

    Object nullReturningMethod();

    Object cyclicMethod();
}

