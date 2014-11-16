package ru.fizteh.fivt.students.vadim_mazaev;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

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
