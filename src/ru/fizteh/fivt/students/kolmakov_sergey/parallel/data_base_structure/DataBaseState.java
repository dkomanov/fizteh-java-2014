package ru.fizteh.fivt.students.kolmakov_sergey.parallel.data_base_structure;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public final class DataBaseState {
    private TableProvider manager;
    private Table currentTable;

    public DataBaseState(TableProvider manager) {
        this.manager = manager;
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(Table table) {
        currentTable = table;
    }

    public TableProvider getManager() {
        return manager;
    }
}
