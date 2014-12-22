package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Tests;

import java.io.IOException;
import java.util.List;

/**
 * Created by ВАНЯ on 20.12.2014.
 */
public interface TestingInterface {
    void noArgumentsMethod();
    void integerArgumentMethod(Integer a);
    void listArgumentMethod(List<Object> list);
    void twoStringArgumentMethod(String str1, String str2);
    void iOExceptionHiMethod() throws IOException;
    void voidReturningMethod();
    Integer zeroReturningMethod();
    Object nullReturningMethod();
    List<Object> cyclicListReturningMethod();
}
