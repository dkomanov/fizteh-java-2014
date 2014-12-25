package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.storage.structured.Table;

/**
 * Created by moskupols on 25.12.14.
 */
public class LoggedAutoCloseableTableAdaptorFactory implements AutoCloseableTableAdaptorFactory {
    private SingleWriterLoggingProxyFactory loggerFactory;

    public LoggedAutoCloseableTableAdaptorFactory(SingleWriterLoggingProxyFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public AutoCloseableTable adapt(AutoCloseableCachingTableProvider provider, Table table) {
        return (AutoCloseableTable) loggerFactory.wrap(
                new AutoCloseableTableAdaptor(provider, table), AutoCloseableTable.class);
    }
}
