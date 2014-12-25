package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.List;

/**
 * Created by moskupols on 25.12.14.
 */
public class AutoCloseableTableAdaptor implements AutoCloseableTable {
    private final AutoCloseableCachingTableProvider provider;
    private Table delegate;

    public AutoCloseableTableAdaptor(AutoCloseableCachingTableProvider provider, Table delegate) {
        this.provider = provider;
        if (delegate == null) {
            throw new IllegalArgumentException("delegate");
        }
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void close() throws IllegalStateException {
        rollback();
        provider.releaseTable(getName());
        delegate = null;
    }

    protected Table getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("already closed");
        }
        return delegate;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        return getDelegate().put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        return getDelegate().remove(key);
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public List<String> list() {
        return getDelegate().list();
    }

    @Override
    public int commit() throws IOException {
        return getDelegate().commit();
    }

    @Override
    public int rollback() {
        return getDelegate().rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return getDelegate().getNumberOfUncommittedChanges();
    }

    @Override
    public int getColumnsCount() {
        return getDelegate().getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return getDelegate().getColumnType(columnIndex);
    }

    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public Storeable get(String key) {
        return getDelegate().get(key);
    }
}
