package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import java.util.List;

public interface LoggingTarget {
    void methodNoArgs();

    void methodIntegerArg(Integer arg);

    void methodStringArg(String arg);

    void methodListArg(List<Object> arg);

    void methodMultipleArgs(Integer arg1, Integer arg2);

    void methodThrows() throws Exception;

    Object methodReturns();

    Object methodCyclic();
}
