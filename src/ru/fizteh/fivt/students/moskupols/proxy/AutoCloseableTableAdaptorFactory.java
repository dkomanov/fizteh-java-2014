package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.storage.structured.Table;

/**
 * Created by moskupols on 25.12.14.
 */
public interface AutoCloseableTableAdaptorFactory {
    AutoCloseableTable adapt(AutoCloseableCachingTableProvider provider, Table table);
}
