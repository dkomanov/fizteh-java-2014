package ru.fizteh.fivt.students.gudkov394.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.gudkov394.Storable.src.CurrentTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kagudkov on 17.11.14.
 */
public class ParallelTable implements Table {
    CurrentTable table;
    ReentrantReadWriteLock lock;
    ParallelTableProvider provider;
    ThreadLocal<ListDiff> diff;

    public ParallelTable(Table newTable, ReentrantReadWriteLock lockTmp, ParallelTableProvider parallelTableProvider) {
        table = (CurrentTable) newTable;
        lock = lockTmp;
        provider = parallelTableProvider;
        diff = new ThreadLocal<ListDiff>();
        diff.set(new ListDiff(table, lock));

    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        lock.writeLock().lock();
        try {
            String old = diff.get().put(key, provider.serialize(this, value));
            if (old == null) {
                return null;
            }
            try {
                return provider.deserialize(this, old);
            } catch (ParseException e) {
                throw new ColumnFormatException(e.getMessage());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = diff.get().remove(key);
        if (oldValue == null) {
            return null;
        }
        try {
            return provider.deserialize(this, oldValue);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return table.size() + diff.get().changedSize();
    }

    @Override
    public List<String> list() {
        return null;
    }

    @Override
    public int commit() throws IOException {
        try {
            return diff.get().commit();
        } catch (ParseException e) {
            System.err.println("We have problem with parsing in commit");
        }
        return 0;
    }

    @Override
    public int rollback() {
        return diff.get().rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return table.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return table.getColumnType(columnIndex);
    }

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = diff.get().get(key);
        if (oldValue == null) {
            return null;
        }
        try {
            return provider.deserialize(table, oldValue);
        } catch (ParseException e) {
            System.err.println("We have a problem with parse in get()");
        }
        return null;
    }

    public String getPath() {
        File providerFile = new File(provider.getPath());
        File table = new File(providerFile, getName());
        return table.getPath();
    }
}
