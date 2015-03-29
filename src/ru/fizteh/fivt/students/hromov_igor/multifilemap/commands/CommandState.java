package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProvider;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBaseTable;

public class CommandState {
    private DBProvider base;
    private DBaseTable usingTable;

    public DBProvider getBase() {
        return base;
    }

    public DBaseTable getUsingTable() {
        return usingTable;
    }

    public void setUsingTable(DBaseTable dataBaseTable) {
        usingTable = dataBaseTable;
    }

    public CommandState(DBProvider b, DBaseTable table) {
        this.base = b;
        this.usingTable = table;
    }
}
