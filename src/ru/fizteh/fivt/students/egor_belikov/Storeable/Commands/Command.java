package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;

public interface Command {
    void execute(String[] args, MyTableProvider myTableProvider) throws Exception;
}
