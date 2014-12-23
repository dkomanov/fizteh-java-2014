package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Created by moskupols on 12.12.14.
 */
public interface StoreableContext {
    TableProvider getProvider();

    Table getCurrentTable();

    void setCurrentTable(Table currentTable);
}
