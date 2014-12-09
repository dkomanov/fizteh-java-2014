package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Created by moskupols on 09.12.14.
 */
public class StoreableContext {
    private final TableProvider provider;
    private Table currentTable;

    public StoreableContext(TableProvider provider) {
        this.provider = provider;
        this.currentTable = null;
    }

    public TableProvider getProvider() {
        return provider;
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }
}
