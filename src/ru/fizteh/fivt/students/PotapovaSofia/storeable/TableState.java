package ru.fizteh.fivt.students.PotapovaSofia.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableState {
    private TableProvider tableProvider;
    private Table usedTable;

    public TableState(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
        usedTable = null;
    }

    public void setUsedTable(Table newUsedTable) {
        usedTable = newUsedTable;
    }

    public Table getUsedTable() {
        return usedTable;
    }

    public TableProvider getTableProvider() {
        return tableProvider;
    }
}
