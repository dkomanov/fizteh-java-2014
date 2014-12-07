package ru.fizteh.fivt.students.gudkov394.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.gudkov394.Parallel.ParallelTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kagudkov on 30.11.14.
 */
public class TableProviderWithToStringAndClose extends ParallelTableProvider implements TableProvider, AutoCloseable {

    private HashMap<String, Table> tables = new HashMap<>();
    AtomicBoolean closed;

    public TableProviderWithToStringAndClose(String dir) {
        super(dir);
        closed = new AtomicBoolean(false);
        for (String table : super.providerFromStoreable.getTableNames()) {
            Table oldTable = super.getTable(table);
            tables.put(table, oldTable);
            lockForTable.put(table, new ReentrantReadWriteLock(true));
        }
    }

    public boolean isClosed() {
        return closed.get();
    }

    private void checkIsClosed() {
        if (isClosed()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void close() throws Exception {
        checkIsClosed();
        lock.writeLock().lock();
        closed.set(true);
        for (Table table : tables.values()) {
            try {
                ((TableWithCloseAndToString) table).close();
            } catch (IllegalFormatException e) {
                System.out.println("table already closed");
            }
        }
        lock.writeLock().unlock();
    }

    @Override
    public Table getTable(String name) {
        checkIsClosed();
        lock.readLock().lock();
        try {
            Table origin = providerFromStoreable.getTable(name);
            if (origin == null) {
                return null;
            }
            if (!tables.containsKey(name)) {
                return null;
            } else {
                if (((TableWithCloseAndToString) tables.get(name)).isClosed()) {
                    tables.put(name, new TableWithCloseAndToString(origin, this, lockForTable.get(name)));
                }
                return tables.get(name);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkIsClosed();
        lock.writeLock().lock();
        try {
            Table origin = providerFromStoreable.createTable(name, columnTypes);
            if (origin == null) {
                return null;
            } else {
                lockForTable.put(name, new ReentrantReadWriteLock(true));
                Table newTable = new TableWithCloseAndToString(origin, this, lockForTable.get(name));
                tables.put(name, newTable);
                return newTable;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        checkIsClosed();
        super.removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        checkIsClosed();
        return super.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkIsClosed();
        return super.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableWithToString(super.createFor(table));
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIsClosed();
        return new StoreableWithToString(super.createFor(table, values));
    }

    @Override
    public List<String> getTableNames() {
        checkIsClosed();
        return super.getTableNames();
    }

    public String toString() {
        checkIsClosed();
        return this.getClass().getSimpleName() + "[" + super.getPath() + "]";
    }
}
