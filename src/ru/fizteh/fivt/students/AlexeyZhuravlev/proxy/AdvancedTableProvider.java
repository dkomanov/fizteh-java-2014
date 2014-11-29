package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.parallel.ParallelTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author AlexeyZhuravlev
 */
public class AdvancedTableProvider extends ParallelTableProvider implements TableProvider, AutoCloseable {

    private AtomicBoolean closed;
    private HashMap<String, Table> tables;

    protected AdvancedTableProvider(String path) throws IOException {
        super(path);
        closed = new AtomicBoolean(false);
        tables = new HashMap<>();
        for (String name: oldProvider.getTableNames()) {
            StructuredTable origin = (StructuredTable) oldProvider.getTable(name);
            tables.put(name, new AdvancedTable(origin, this, tableLocks.get(name)));
            tableLocks.put(name, new ReentrantReadWriteLock(true));
        }
    }

    @Override
    public Table getTable(String name) {
        checkClosed();
        lock.readLock().lock();
        try {
            StructuredTable origin = (StructuredTable) oldProvider.getTable(name);
            if (origin == null) {
                return null;
            }
            if (!tables.containsKey(name)) {
                return null;
            } else {
                if (((AdvancedTable) tables.get(name)).isClosed()) {
                    tables.put(name, new AdvancedTable(origin, this, tableLocks.get(name)));
                }
                return tables.get(name);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkClosed();
        lock.writeLock().lock();
        try {
            StructuredTable origin = (StructuredTable) oldProvider.createTable(name, columnTypes);
            if (origin == null) {
                return null;
            } else {
                tableLocks.put(name, new ReentrantReadWriteLock(true));
                Table newTable = new AdvancedTable(origin, this, tableLocks.get(name));
                tables.put(name, newTable);
                return newTable;
            }
        } finally {
            lock.writeLock().unlock();
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
        lock.writeLock().lock();
        closed.set(true);
        for (String name: tables.keySet()) {
            try {
                ((AdvancedTable) tables.get(name)).close();
            } catch (IllegalStateException e) {
                continue;
            }
        }
        lock.writeLock().unlock();
    }
}
