package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelTable implements Table {
    public ParallelTable(StoreableTable givenTable, ParallelTableProvider givenProvider,
                         ReentrantReadWriteLock givenLock) {
        table = givenTable;
        provider = givenProvider;
        lock = givenLock;
        multiThread = new ThreadLocal<MultiThreadManager>() {
            @Override
            protected MultiThreadManager initialValue() {
                return new MultiThreadManager(table, lock);
            }
        };
    }

    private StoreableTable table;
    private ParallelTableProvider provider;
    private ReentrantReadWriteLock lock;
    private ThreadLocal<MultiThreadManager> multiThread;

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = multiThread.get().get(key);
        if (oldValue == null) {
            return null;
        }
        try {
            return provider.deserialize(this, oldValue);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = multiThread.get().put(key, provider.serialize(this, value));
        if (oldValue == null) {
            return null;
        }
        try {
            return provider.deserialize(this, oldValue);
        } catch (ParseException ex) {
            throw new ColumnFormatException(ex.getMessage());
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = multiThread.get().remove(key);
        if (oldValue == null) {
            return null;
        }
        try {
            return provider.deserialize(this, oldValue);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return table.size() + multiThread.get().diffCount();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        lock.readLock().lock();
        try {
            List<String> listTables = table.list();
            listTables.addAll(multiThread.get().getCreations());
            listTables.removeAll(multiThread.get().getDeletions());
            return listTables;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        int changesCount = multiThread.get().changesCount();
        multiThread.get().commit();
        multiThread.get().clear();
        return changesCount;
    }

    @Override
    public int rollback() {
        int changesCount = multiThread.get().changesCount();
        multiThread.get().clear();
        return changesCount;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return multiThread.get().changesCount();
    }

    @Override
    public int getColumnsCount() {
        return table.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) {
        return table.getColumnType(columnIndex);
    }

    public Table getTable() {
        return table;
    }
}
