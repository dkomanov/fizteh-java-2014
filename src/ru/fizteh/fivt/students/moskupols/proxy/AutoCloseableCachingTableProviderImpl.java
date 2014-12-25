package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.moskupols.parallel.CachingTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by moskupols on 25.12.14.
 */
public class AutoCloseableCachingTableProviderImpl
        implements AutoCloseableCachingTableProvider {

    private CachingTableProvider delegate;
    private AutoCloseableTableAdaptorFactory adaptorFactory;

    public AutoCloseableCachingTableProviderImpl(
            CachingTableProvider delegate, AutoCloseableTableAdaptorFactory adaptorFactory) {
        this.delegate = delegate;
        this.adaptorFactory = adaptorFactory;
    }

    protected CachingTableProvider getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("already closed");
        }
        return delegate;
    }

    @Override
    public void close() throws Exception {
        for (String name : getTableNames()) {
            getTable(name).close();
        }
        delegate = null;
    }

    @Override
    public void releaseTable(String name) {
        getDelegate().releaseTable(name);
    }

    @Override
    public AutoCloseableTable getTable(String name) {
        final Table ret = getDelegate().getTable(name);
        return ret == null ? null : adaptorFactory.adapt(this, ret);
    }

    @Override
    public AutoCloseableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        final Table ret = getDelegate().createTable(name, columnTypes);
        return ret == null ? null : adaptorFactory.adapt(this, ret);
    }

    @Override
    public void removeTable(String name) throws IOException {
        getDelegate().removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return getDelegate().deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return getDelegate().serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return getDelegate().createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return getDelegate().createFor(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return getDelegate().getTableNames();
    }
}
