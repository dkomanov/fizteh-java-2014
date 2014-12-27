package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public final class DataBaseState {
    private TableProvider manager;
    private Table usedTable;

    public DataBaseState(TableProvider manager) {
        this.manager = manager;
        usedTable = null;
    }

    public void setUsedTable(Table newUsedTable) {
        usedTable = newUsedTable;
    }

    public Table getUsedTable() {
        return usedTable;
    }

    public TableProvider getManager() {
        return manager;
    }
}
