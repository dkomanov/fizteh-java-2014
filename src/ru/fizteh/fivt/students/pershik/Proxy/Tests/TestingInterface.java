package ru.fizteh.fivt.students.pershik.Proxy.Tests;

import java.io.IOException;
import java.util.List;

/**
 * Created by pershik on 11/26/14.
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
