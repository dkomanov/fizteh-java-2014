package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public final class DataBaseState {
    private TableProvider manager;
    private Table currentTable;

    public DataBaseState(TableProvider manager) {
        this.manager = manager;
        currentTable = null;
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
