package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.test;

import java.util.List;

public interface LoggingTarget {
    public void methodNoArgs();

    public void methodIntegerArg(Integer arg);

    public void methodStringArg(String arg);

    public void methodListArg(List<Object> arg);

    public void methodMultipleArgs(Integer arg1, Integer arg2);

    public void methodThrows() throws Exception;

    public Object methodReturns();

    public Object methodCyclic();
}
