package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTable;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelTableProvider implements TableProvider {
    public ParallelTableProvider(String dbDir) throws IOException {
        StoreableTableProviderFactory factory = new StoreableTableProviderFactory();
        provider = (StoreableTableProvider) factory.create(dbDir);
        lock = new ReentrantReadWriteLock(true);
        tables = new HashMap<>();
        for (String tableName : provider.getTableNames()) {
            ReentrantReadWriteLock tempLock = new ReentrantReadWriteLock(true);
            StoreableTable table = (StoreableTable) provider.getTable(tableName);
            tables.put(tableName, new ParallelTable(table, this, tempLock));
        }
    }

    private StoreableTableProvider provider;
    private HashMap<String, Table> tables;
    private ReentrantReadWriteLock lock;

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        lock.readLock().lock();
        try {
            if (!tables.containsKey(name)) {
                return null;
            }
            return tables.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        lock.writeLock().lock();
        try {
            if (tables.containsKey(name)) {
                return null;
            }
            StoreableTable table = (StoreableTable) provider.createTable(name, columnTypes);
            ReentrantReadWriteLock tempLock = new ReentrantReadWriteLock(true);
            tables.put(name, new ParallelTable(table, this, tempLock));
            return tables.get(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        lock.writeLock().lock();
        try {
            provider.removeTable(name);
            tables.remove(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return provider.deserialize(((ParallelTable) table).getTable(), value);
    }

    @Override
    public String serialize(Table table, Storeable value) {
        return provider.serialize(((ParallelTable) table).getTable(), value);
    }

    @Override
    public Storeable createFor(Table table) {
        return provider.createFor(((ParallelTable) table).getTable());
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        return provider.createFor(((ParallelTable) table).getTable(), values);
    }

    @Override
    public List<String> getTableNames() {
        lock.readLock().lock();
        try {
            return provider.getTableNames();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Table getUsing() {
        return provider.getUsing();
    }

    public Table setUsing(String name) {
        return provider.setUsing(name);
    }
}
