package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andrey_reshetnikov.Parallel.ParallelTableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AdvancedTableProvider extends ParallelTableProvider implements AutoCloseable {

    private AtomicBoolean closed;

    protected AdvancedTableProvider(String path) throws IOException {
        super(path);
        closed = new AtomicBoolean(false);
        tablesLocks = new HashMap<>();
        for (String name: oldProvider.getTableNames()) {
            MyStoreableTable origin = (MyStoreableTable) oldProvider.getTable(name);
            tablesLocks.put(name, new AdvancedTable(origin, this, new ReentrantReadWriteLock(true)));
        }
    }

    @Override
    public Table getTable(String name) {
        checkClosed();
        myLock.readLock().lock();
        try {
            MyStoreableTable origin = (MyStoreableTable) oldProvider.getTable(name);
            if (origin == null) {
                return null;
            }
            if (!tablesLocks.containsKey(name)) {
                return null;
            } else {
                if (((AdvancedTable) tablesLocks.get(name)).isClosed()) {
                    tablesLocks.put(name, new AdvancedTable(origin, this, new ReentrantReadWriteLock(true)));
                }
                return tablesLocks.get(name);
            }
        } finally {
            myLock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkClosed();
        myLock.writeLock().lock();
        try {
            MyStoreableTable origin = (MyStoreableTable) oldProvider.createTable(name, columnTypes);
            if (origin == null) {
                return null;
            } else {
                Table newTable = new AdvancedTable(origin, this, new ReentrantReadWriteLock(true));
                tablesLocks.put(name, newTable);
                return newTable;
            }
        } finally {
            myLock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        checkClosed();
        super.removeTable(name);
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();
        return new AdvancedStoreableValue(super.createFor(table));
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        return new AdvancedStoreableValue(super.createFor(table, values));
    }

    @Override
    public List<String> getTableNames() {
        checkClosed();
        return super.getTableNames();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        checkClosed();
        return new AdvancedStoreableValue(super.deserialize(table, value));
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkClosed();
        return super.serialize(table, ((AdvancedStoreableValue) value).getOrigin());
    }

    private void checkClosed() throws IllegalStateException {
        if (closed.get()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void close() throws Exception {
        checkClosed();
        myLock.writeLock().lock();
        closed.set(true);
        for (String name: tablesLocks.keySet()) {
            try {
                ((AdvancedTable) tablesLocks.get(name)).close();
            } catch (IllegalStateException e) {
                // Do nothing
            }
        }
        myLock.writeLock().unlock();
    }

    @Override
    public String toString() {
        checkClosed();
        String path = super.getPath();
        return this.getClass().getSimpleName() + "[" + path + "]";
    }
}
