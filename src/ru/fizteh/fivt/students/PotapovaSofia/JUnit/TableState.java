package ru.fizteh.fivt.students.PotapovaSofia.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

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
