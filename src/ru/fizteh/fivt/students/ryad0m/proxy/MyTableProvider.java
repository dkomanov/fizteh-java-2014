package ru.fizteh.fivt.students.ryad0m.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTableProvider implements TableProvider, AutoCloseable {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path location;
    private AtomicBoolean closed = new AtomicBoolean(false);
    private HashMap<String, StructedTable> tables;


    public MyTableProvider(Path path) throws IOException {
        location = path;
        tables = new HashMap<String, StructedTable>();
        if (!location.toFile().exists()) {
            location.toFile().mkdirs();
        }
        if (!location.toFile().isDirectory()) {
            throw new IOException("it isn't a directory");
        }
        File[] tableDirs = location.toFile().listFiles();
        for (File table : tableDirs) {
            if (table.exists() && table.isDirectory()) {
                StructedTable newDirTable = new StructedTable(table.toPath());
                tables.put(table.getName(), newDirTable);
            }
        }
    }

    @Override
    public Table getTable(String name) {
        Table res;
        lock.readLock().lock();
        try {
            checkClosed();
            if (name == null) {
                throw new IllegalArgumentException();
            }
            if (tables.containsKey(name)) {
                res = new TransactionTable(tables.get(name));
            } else {
                res = null;
            }
        } finally {
            lock.readLock().unlock();
        }
        return res;
    }

    private boolean checkName(String s) {
        checkClosed();
        if (s == null) {
            return false;
        }
        return !s.contains("/");
    }

    @Override
    public String toString() {
        checkClosed();
        return "MyTableProvider" + "[" + location.toString() + "]";
    }


    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        StructedTable res;
        lock.writeLock().lock();
        try {
            checkClosed();
            if (!checkName(name)) {
                throw new IllegalArgumentException();
            }
            if (tables.containsKey(name)) {
                res = null;
            } else {
                res = new StructedTable(location.resolve(name).normalize(), columnTypes);
                tables.put(name, res);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (res == null) {
            return null;
        }
        return new TransactionTable(res);
    }

    @Override
    public void removeTable(String name) throws IOException {
        lock.writeLock().lock();
        try {
            checkClosed();
            if (name == null) {
                throw new IllegalArgumentException();
            }
            if (!tables.containsKey(name)) {
                throw new IllegalStateException();
            }
            tables.get(name).deleteData();
            tables.remove(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> list;
        lock.readLock().lock();
        try {
            checkClosed();
            list = ((TransactionTable) table).getStructedTable().getColumnTypes();
        } finally {
            lock.readLock().unlock();
        }
        return new MyStorable(list, XmlSerializer.deserializeString(value, list));
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        lock.readLock().lock();
        try {
            checkClosed();
            ((TransactionTable) table).getStructedTable().checkIntegrity(value);
        } finally {
            lock.readLock().unlock();
        }
        return XmlSerializer.serializeObjectList(((MyStorable) value).getValues());
    }

    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> list;
        lock.readLock().lock();
        try {
            checkClosed();
            list = ((TransactionTable) table).getStructedTable().getColumnTypes();
        } finally {
            lock.readLock().unlock();
        }
        return new MyStorable(list);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> list;
        lock.readLock().lock();
        try {
            checkClosed();
            list = ((TransactionTable) table).getStructedTable().getColumnTypes();
        } finally {
            lock.readLock().unlock();
        }
        return new MyStorable(list, values);
    }

    @Override
    public List<String> getTableNames() {
        lock.readLock().lock();
        List<String> res;
        try {
            checkClosed();
            res = new ArrayList<String>(tables.keySet());
        } finally {
            lock.readLock().unlock();
        }
        return res;
    }

    private void checkClosed() {
        if (closed.get()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void close() throws Exception {
        lock.writeLock().lock();
        try {
            closed.set(true);
            for (StructedTable table : tables.values()) {
                try {
                    table.close();
                } catch (Exception ex) {
                    //Do Nothing
                }
            }

        } finally {
            lock.writeLock().unlock();
        }
    }
}
