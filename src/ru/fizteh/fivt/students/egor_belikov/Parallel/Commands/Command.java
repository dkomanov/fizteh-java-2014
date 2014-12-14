package ru.fizteh.fivt.students.egor_belikov.Parallel.Commands;

import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;

public interface Command {

    void execute(String args[], MyTableProvider myTableProvider) throws Exception;
}
