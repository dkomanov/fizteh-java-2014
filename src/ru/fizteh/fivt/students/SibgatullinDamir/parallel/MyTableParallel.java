package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Lenovo on 02.12.2014.
 */
public class MyTableParallel implements Table {

    MyTable originalTable;
    ThreadLocal<DiffParallel> diff;
    MyTableProviderParallel provider;
    ReentrantReadWriteLock lock;

    public MyTableParallel(MyTable passedTable, MyTableProviderParallel passedProvider,
                           ReentrantReadWriteLock passedLock) {
        originalTable = passedTable;
        provider = passedProvider;
        lock = passedLock;
        diff = new ThreadLocal<DiffParallel>() {
            @Override
            protected DiffParallel initialValue() {
                return new DiffParallel(originalTable, lock);
            }
        };
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        try {
            String oldValue = diff.get().put(key, provider.serialize(this, value));
            return provider.deserialize(this, oldValue);
        } catch (ParseException e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = diff.get().remove(key);
        try {
            return provider.deserialize(this, oldValue);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public int size() {

        lock.readLock().lock();
        try {
            int originalSize = originalTable.size();
            return originalSize + diff.get().deltaSize();
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public List<String> list() {

        lock.readLock().lock();
        try {
            return originalTable.list();
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public int commit() throws IOException {
        int result = diff.get().commit();
        diff.get().clear();
        return result;
    }

    @Override
    public int rollback() {
        int result = diff.get().clear();
        return result;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return diff.get().changesCount();
    }

    @Override
    public int getColumnsCount() {
        return originalTable.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return originalTable.getColumnType(columnIndex);
    }

    @Override
    public String getName() {
        return originalTable.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = diff.get().get(key);
        try {
            return provider.deserialize(this, oldValue);
        } catch (ParseException e) {
            return null;
        }
    }

    public Table getMyTable() {
        return originalTable;
    }

}