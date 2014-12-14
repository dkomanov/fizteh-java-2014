package ru.fizteh.fivt.students.moskupols.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by moskupols on 14.12.14.
 */
public class ThreadSafeCachingTableProvider implements TableProvider {
    private final TableProvider delegate;
    private final Map<String, Table> tableMap;
    Lock tableMapLock;

    public ThreadSafeCachingTableProvider(TableProvider delegate) {
        this.delegate = delegate;
        tableMap = new HashMap<>();
        tableMapLock = new ReentrantLock();
    }

    @Override
    public Table getTable(String name) {
        tableMapLock.lock();
        Table ret = tableMap.get(name);
        if (ret == null) {
            ret = delegate.getTable(name);
            if (ret != null)
                tableMap.put(name, ret);
        }
        tableMapLock.unlock();
        return ret;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        tableMapLock.lock();
        if (tableMap.containsKey(name)) {
            return null;
        }
        Table ret = delegate.createTable(name, columnTypes);
        tableMap.put(name, ret);
        tableMapLock.unlock();
        return ret;
    }

    @Override
    public void removeTable(String name) throws IOException {
        tableMapLock.lock();
        delegate.removeTable(name);
        tableMap.remove(name);
        tableMapLock.unlock();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return delegate.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return delegate.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return delegate.createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return delegate.createFor(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return delegate.getTableNames();
    }
}
