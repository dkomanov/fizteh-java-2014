package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public final class TableConnector {
    private TableProvider manager;
    private Table usedTable;
    
    public TableConnector(TableProvider manager) {
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
