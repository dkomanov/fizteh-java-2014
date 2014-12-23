package ru.fizteh.fivt.students.andrey_reshetnikov.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelTableProvider implements TableProvider{

    protected HashMap<String, Table> tablesLocks;
    protected MyStoreableTableProvider oldProvider;
    protected final ReentrantReadWriteLock myLock = new ReentrantReadWriteLock();

    protected ParallelTableProvider(String path) throws IOException {
        MyStoreableTableProviderFactory factory = new MyStoreableTableProviderFactory();
        tablesLocks = new HashMap<>();
        oldProvider = (MyStoreableTableProvider) factory.create(path);
        for (String name: oldProvider.getTableNames()) {
            ReentrantReadWriteLock myLock = new ReentrantReadWriteLock(true);
            MyStoreableTable origin = (MyStoreableTable) oldProvider.getTable(name);
            tablesLocks.put(name, new ParallelTable(origin, this, myLock));
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        myLock.readLock().lock();
        try {
            if (!tablesLocks.containsKey(name)) {
                return null;
            } else {
                return tablesLocks.get(name);
            }
        } finally {
            myLock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        myLock.writeLock().lock();
        try {
            if (tablesLocks.containsKey(name)) {
                return null;
            } else {
                MyStoreableTable origin = (MyStoreableTable) oldProvider.createTable(name, columnTypes);
                ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
                tablesLocks.put(name, new ParallelTable(origin, this, lock));
                return tablesLocks.get(name);
            }
        } finally {
            myLock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        myLock.writeLock().lock();
        try {
            oldProvider.removeTable(name);
            tablesLocks.remove(name);
        } finally {
            myLock.writeLock().unlock();
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return oldProvider.serialize(((ParallelTable) table).getStructuredTable(), value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return oldProvider.deserialize(((ParallelTable) table).getStructuredTable(), value);
    }

    @Override
    public Storeable createFor(Table table) {
        myLock.readLock().lock();
        try {
            return oldProvider.createFor(((ParallelTable) table).getStructuredTable());
        } finally {
            myLock.readLock().lock();
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        myLock.readLock().lock();
        try {
            return oldProvider.createFor(((ParallelTable) table).getStructuredTable(), values);
        } finally {
            myLock.readLock().lock();
        }
    }

    @Override
    public List<String> getTableNames() {
        myLock.readLock().lock();
        try {
            return oldProvider.getTableNames();
        } finally {
            myLock.readLock().unlock();
        }
    }

    public String getPath() {
        return oldProvider.getPath();
    }
}
