package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.test;

import java.util.ArrayList;
import java.util.List;

public class LoggingTargetImpl implements LoggingTarget {
    @Override
    public void methodNoArgs() {
    }

    @Override
    public void methodIntegerArg(Integer arg) {
    }

    @Override
    public void methodStringArg(String arg) {
    }

    @Override
    public void methodListArg(List<Object> arg) {
    }

    @Override
    public void methodMultipleArgs(Integer arg1, Integer arg2) {
    }

    @Override
    public void methodThrows() throws Exception {
        throw new IllegalStateException("exception");
    }

    @Override
    public Object methodReturns() {
        return 0;
    }

    @Override
    public Object methodCyclic() {
        List<Object> list = new ArrayList<>();
        List<Object> sublist = new ArrayList<>();
        sublist.add(list);
        list.add(sublist);
        return list;
    }
}
