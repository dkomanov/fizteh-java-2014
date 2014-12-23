package ru.fizteh.fivt.students.andrey_reshetnikov.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelTable implements Table{

    private final MyStoreableTable originalTable;
    private final ThreadLocal<Changes> changes;
    private final ParallelTableProvider parallelTableProvider;
    private final ReentrantReadWriteLock myLock;

    public ParallelTable(MyStoreableTable origin, ParallelTableProvider passedProvider,
                         ReentrantReadWriteLock passedLock) {
        originalTable = origin;
        parallelTableProvider = passedProvider;
        myLock = passedLock;
        changes = new ThreadLocal<Changes>() {
            @Override
            protected Changes initialValue() {
                return new Changes(originalTable, myLock);
            }
        };
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String old = changes.get().get(key);
        if (old == null) {
            return null;
        }
        try {
            return parallelTableProvider.deserialize(this, old);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        try {
            String old = changes.get().put(key, parallelTableProvider.serialize(this, value));
            if (old == null) {
                return null;
            }
            return parallelTableProvider.deserialize(this, old);
        } catch (ParseException e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String old = changes.get().remove(key);
        if (old == null) {
            return null;
        }
        try {
            return parallelTableProvider.deserialize(this, old);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public int size() {
        myLock.readLock().lock();
        try {
            int originalSize = originalTable.size();
            return originalSize + changes.get().deltaSize();
        } finally {
            myLock.readLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        myLock.readLock().lock();
        try {
            List<String> list = originalTable.list();
            list.addAll(changes.get().getCreated());
            list.removeAll(changes.get().getRemoved());
            return list;
        } finally {
            myLock.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        int result = changes.get().differencesCount();
        changes.get().commit();
        changes.get().clear();
        return result;
    }

    @Override
    public int rollback() {
        int result = changes.get().differencesCount();
        changes.get().clear();
        return result;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return changes.get().differencesCount();
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

    public Table getStructuredTable() {
        return originalTable;
    }

    public String getPath() {
        File providerFile = new File(parallelTableProvider.getPath());
        File table = new File(providerFile, getName());
        return table.getPath();
    }
}
