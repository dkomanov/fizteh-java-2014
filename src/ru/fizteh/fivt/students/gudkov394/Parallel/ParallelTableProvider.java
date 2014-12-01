package ru.fizteh.fivt.students.gudkov394.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.gudkov394.Storable.src.Junit;
import ru.fizteh.fivt.students.gudkov394.Storable.src.TableProviderClass;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kagudkov on 17.11.14.
 */
public class ParallelTableProvider implements TableProvider {
    public TableProviderClass providerFromStoreable = null;
    protected ReentrantReadWriteLock lock = null;
    protected Map<String, ReentrantReadWriteLock> lockForTable = null;
    String path;
    public ParallelTableProvider(String dir) {
        path = dir;
        Junit tableProviderFactory = new Junit();
        providerFromStoreable = tableProviderFactory.create(dir);
        lock = new ReentrantReadWriteLock(true);
        lockForTable = new HashMap<>();
        for (String s : providerFromStoreable.tables.keySet()) {
            lockForTable.put(s, new ReentrantReadWriteLock(true));
        }
    }

    @Override
    public Table getTable(String name) {
        lock.readLock().lock();
        try {
            Table table = providerFromStoreable.getTable(name);
            if (table == null) {
                return null;
            }
            return new ParallelTable(table, lockForTable.get(table.getName()), this);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        lock.writeLock().lock();
        try {
            Table newTable = providerFromStoreable.createTable(name, columnTypes);
            if (newTable == null) {
                return null;
            }
            lockForTable.put(newTable.getName(), new ReentrantReadWriteLock(true));
            return new ParallelTable(newTable, lockForTable.get(newTable.getName()), this);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        lock.writeLock().lock();
        try {
            providerFromStoreable.removeTable(name);
            lockForTable.remove(name);
        } finally {
            lock.writeLock().unlock();
        }
        return;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return providerFromStoreable.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return providerFromStoreable.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return providerFromStoreable.createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return providerFromStoreable.createFor(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return providerFromStoreable.getTableNames();
    }

    protected String getPath() {
        return path;
    }


}

