package ru.fizteh.fivt.students.sautin1.telnet.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.GeneralTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sautin1 on 12/9/14.
 */
public class StoreableTable extends GeneralTable<Storeable> implements Table, AutoCloseable {
    private final List<Class<?>> valueTypes;
    private final StoreableTableProvider provider;
    private boolean isClosed;

    StoreableTable(String name, boolean autoCommit, List<Class<?>> valueTypes, StoreableTableProvider provider) {
        super(name, autoCommit, provider);
        if (valueTypes == null) {
            throw new IllegalArgumentException("Value types are not defined");
        }
        this.valueTypes = Collections.unmodifiableList(new ArrayList<>(valueTypes));
        this.provider = provider;
        this.isClosed = false;
    }

    StoreableTable(String name, List<Class<?>> valueTypes, StoreableTableProvider provider) {
        this(name, true, valueTypes, provider);
    }

    @Override
    public int size() {
        checkClosed();
        return super.size();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        checkClosed();
        return super.getNumberOfUncommittedChanges();
    }

    @Override
    public String getName() {
        checkClosed();
        return super.getName();
    }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return valueTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return valueTypes.get(columnIndex);
    }

    public List<Class<?>> getColumnTypes() {
        checkClosed();
        return valueTypes;
    }

    @Override
    public Storeable put(String key, Storeable value) {
        checkClosed();
        return super.put(key, value);
    }

    @Override
    public Storeable get(String key) {
        checkClosed();
        return super.get(key);
    }

    @Override
    public Storeable remove(String key) {
        checkClosed();
        return super.remove(key);
    }

    @Override
    public int commit() {
        checkClosed();
        return super.commit();
    }

    @Override
    public int rollback() {
        checkClosed();
        return super.rollback();
    }

    @Override
    public List<String> list() {
        checkClosed();
        return super.list();
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        rollback();
        provider.closeTable(getName());
        isClosed = true;
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("table is closed");
        }
    }

    @Override
    public String toString() {
        checkClosed();
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");
        builder.append(provider.getRootDir().resolve(getName()));
        builder.append("]");

        return builder.toString();
    }
}
