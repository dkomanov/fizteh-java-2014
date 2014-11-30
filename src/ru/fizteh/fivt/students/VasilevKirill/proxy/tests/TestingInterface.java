package ru.fizteh.fivt.students.VasilevKirill.proxy.tests;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kirill on 30.11.2014.
 */
public interface TestingInterface {
    void noArgumentsMethod();
    void oneArgumentMethod(String a);
    void twoArgumentMethod(Integer a, Integer b);
    void listArgumentsMethod(List<String> list);
    void cyclicArgumentsMethod(List<Object> list);
    Integer smthReturningMethod();
    Object nullReturningMethod();
    Integer exceptionMethod() throws IOException;
}
