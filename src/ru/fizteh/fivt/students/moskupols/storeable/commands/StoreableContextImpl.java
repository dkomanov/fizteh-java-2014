package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Created by moskupols on 09.12.14.
 */
public class StoreableContextImpl implements StoreableContext {
    private final TableProvider provider;
    private Table currentTable;

    public StoreableContextImpl(TableProvider provider) {
        this.provider = provider;
        this.currentTable = null;
    }

    @Override
    public TableProvider getProvider() {
        return provider;
    }

    @Override
    public Table getCurrentTable() {
        return currentTable;
    }

    @Override
    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }
}
