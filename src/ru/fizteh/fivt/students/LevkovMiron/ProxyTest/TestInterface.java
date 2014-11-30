package ru.fizteh.fivt.students.LevkovMiron.ProxyTest;

import java.io.IOException;
import java.util.List;

/**
 * Created by Мирон on 30.11.2014 ru.fizteh.fivt.students.LevkovMiron.ProxyTest.
 */
public interface TestInterface {

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
