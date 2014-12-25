package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

/**
 * Created by moskupols on 25.12.14.
 */
public interface AutoCloseableTableProviderFactory extends TableProviderFactory, AutoCloseable {
}
