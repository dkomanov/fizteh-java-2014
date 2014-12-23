package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andrey_reshetnikov.Parallel.ParallelTable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Parallel.ParallelTableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AdvancedTable extends ParallelTable implements Table, AutoCloseable {

    AtomicBoolean closed;

    public AdvancedTable(MyStoreableTable origin, ParallelTableProvider passedProvider,
                         ReentrantReadWriteLock passedLock) {
        super(origin, passedProvider, passedLock);
        closed = new AtomicBoolean(false);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkClosed();
        Storeable origin = super.put(key, value);
        if (origin == null) {
            return null;
        } else {
            if (origin.getClass() == AdvancedStoreableValue.class) {
                return origin;
            } else {
                return new AdvancedStoreableValue(origin);
            }
        }
    }

    @Override
    public Storeable remove(String key) {
        checkClosed();
        Storeable origin = super.remove(key);
        if (origin == null) {
            return null;
        } else {
            if (origin.getClass() == AdvancedStoreableValue.class) {
                return origin;
            } else {
                return new AdvancedStoreableValue(origin);
            }
        }
    }

    @Override
    public int size() {
        checkClosed();
        return super.size();
    }

    @Override
    public List<String> list() {
        checkClosed();
        return super.list();
    }

    @Override
    public int commit() throws IOException {
        checkClosed();
        return super.commit();
    }

    @Override
    public int rollback() {
        checkClosed();
        return super.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        checkClosed();
        return super.getNumberOfUncommittedChanges();
    }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return super.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return super.getColumnType(columnIndex);
    }

    @Override
    public String getName() {
        checkClosed();
        return super.getName();
    }

    @Override
    public Storeable get(String key) {
        checkClosed();
        Storeable origin = super.get(key);
        if (origin == null) {
            return null;
        } else {
            if (origin.getClass() == AdvancedStoreableValue.class) {
                return origin;
            } else {
                return new AdvancedStoreableValue(origin);
            }
        }
    }

    @Override
    public void close() throws Exception {
        checkClosed();
        rollback();
        closed.set(true);
    }

    @Override
    public String toString() {
        checkClosed();
        return super.getPath();
    }

    private void checkClosed() throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException();
        }
    }

    public boolean isClosed() {
        return closed.get();
    }

    public MyStoreableTable getStructuredTable() {
        return (MyStoreableTable) super.getStructuredTable();
    }
}
