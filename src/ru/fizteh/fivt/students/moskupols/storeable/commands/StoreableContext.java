package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.students.moskupols.storeable.KnownDiffStructuredTable;
import ru.fizteh.fivt.students.moskupols.storeable.KnownDiffStructuredTableProvider;

/**
 * Created by moskupols on 09.12.14.
 */
public class StoreableContext {
    private final KnownDiffStructuredTableProvider provider;
    private KnownDiffStructuredTable currentTable;

    public StoreableContext(KnownDiffStructuredTableProvider provider) {
        this.provider = provider;
        this.currentTable = null;
    }

    public KnownDiffStructuredTableProvider getProvider() {
        return provider;
    }

    public KnownDiffStructuredTable getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(KnownDiffStructuredTable currentTable) {
        this.currentTable = currentTable;
    }
}
