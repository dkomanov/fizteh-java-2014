package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Lenovo on 02.12.2014.
 */
public class MyTableProviderParallel implements TableProvider {

    MyTableProvider myProvider;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    HashMap<String, ReentrantReadWriteLock> locks;

    protected MyTableProviderParallel(String path) throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        myProvider = (MyTableProvider) factory.create(path);
        locks = new HashMap<>();
        for (String name: myProvider.getTableNames()) {
            locks.put(name, new ReentrantReadWriteLock(true));
        }
    }

    @Override
    public Table getTable(String name) {

        lock.readLock().lock();
        try {
            MyTable table = (MyTable) myProvider.getTable(name);
            if (table == null) {
                return null;
            } else {
                return new MyTableParallel(table, this, locks.get(name));
            }
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {

        lock.writeLock().lock();
        try {
            MyTable origin = (MyTable) myProvider.createTable(name, columnTypes);
            if (origin == null) {
                return null;
            } else {
                locks.put(name, new ReentrantReadWriteLock(true));
                return new MyTableParallel(origin, this, locks.get(name));
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void removeTable(String name) throws IOException {

        lock.writeLock().lock();
        try {
            myProvider.removeTable(name);
            locks.remove(name);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public Storeable createFor(Table table) {
        return myProvider.createFor(((MyTableParallel) table).getMyTable());
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return myProvider.createFor(((MyTableParallel) table).getMyTable(), values);
    }

    @Override
    public List<String> getTableNames() {

        lock.readLock().lock();
        try {
            return myProvider.getTableNames();
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return myProvider.deserialize(((MyTableParallel) table).getMyTable(), value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return myProvider.serialize(((MyTableParallel) table).getMyTable(), value);
    }
}
