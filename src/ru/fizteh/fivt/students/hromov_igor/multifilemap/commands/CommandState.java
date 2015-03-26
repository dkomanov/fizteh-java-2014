package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProvider;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBaseTable;

public class CommandState {
    public DBProvider base;
    public DBaseTable usingTable;

    public CommandState(DBProvider b, DBaseTable table) {
        this.base = b;
        this.usingTable = table;
    }
}
