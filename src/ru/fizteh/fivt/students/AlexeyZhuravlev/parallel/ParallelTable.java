package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author AlexeyZhuravlev
 */
public class ParallelTable implements Table {

    StructuredTable originalTable;
    ThreadLocal<Diff> diff;
    ParallelTableProvider provider;
    ReentrantReadWriteLock lock;

    public ParallelTable(StructuredTable origin, ParallelTableProvider passedProvider,
                         ReentrantReadWriteLock passedLock) {
        originalTable = origin;
        provider = passedProvider;
        lock = passedLock;
        diff = new ThreadLocal<Diff>() {
            @Override
            protected Diff initialValue() {
                return new Diff(originalTable, lock);
            }
        };
    }

    public String getPath() {
        File providerFile = new File(provider.getPath());
        File table = new File(providerFile, getName());
        return table.getPath();
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        try {
            String old = diff.get().put(key, provider.serialize(this, value));
            if (old == null) {
                return null;
            }
            return provider.deserialize(this, old);
        } catch (ParseException e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String old = diff.get().remove(key);
        if (old == null) {
            return null;
        }
        try {
            return provider.deserialize(this, old);
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
            List<String> list = originalTable.list();
            list.addAll(diff.get().getCreated().stream().collect(Collectors.toList()));
            list.removeAll(diff.get().getRemoved().stream().collect(Collectors.toList()));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        int result = diff.get().changesCount();
        diff.get().commit();
        diff.get().clear();
        return result;
    }

    @Override
    public int rollback() {
        int result = diff.get().changesCount();
        diff.get().clear();
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
        String old = diff.get().get(key);
        if (old == null) {
            return null;
        }
        try {
            return provider.deserialize(this, old);
        } catch (ParseException e) {
            return null;
        }
    }

    public Table getStructuredTable() {
        return originalTable;
    }

}
