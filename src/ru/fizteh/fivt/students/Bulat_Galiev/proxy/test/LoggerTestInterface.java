package ru.fizteh.fivt.students.Bulat_Galiev.proxy.test;

import java.util.List;

public interface LoggerTestInterface {
    void noArguments();

    void returnVoid(int argument);

    List<Object> listArgument(List<Object> list);

    Integer manyArguments(Integer a, Integer b);

    void exception() throws Exception;

    void voidClass();

    Object returnNull();
}
