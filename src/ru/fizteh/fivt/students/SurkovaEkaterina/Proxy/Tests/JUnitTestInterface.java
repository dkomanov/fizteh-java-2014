package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Tests;

import java.util.List;

public interface JUnitTestInterface {
    void execute();

    void supportingMethod() throws Exception;

    int getAmount();

    String arrayGetter(List<String> types, int number);
}
