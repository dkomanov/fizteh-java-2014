package ru.fizteh.fivt.students.VasilevKirill.telnet.structures;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Kirill on 26.11.2014.
 */
public class SharedMyTable implements Table {
    private ThreadLocal<MyTable> threadLocal;
    private volatile Map<String, Storeable> data;

    public SharedMyTable(MyTable myTable) throws IOException {
        threadLocal = new ThreadLocal<MyTable>() {
            @Override
            protected MyTable initialValue() {
                return myTable;
            }
        };
        if (threadLocal == null) {
            throw new IOException("SharedTable: threadLocal wasn't initialized");
        }
        data = threadLocal.get().data;
    }

    @Override
    public String getName() {
        return threadLocal.get().getName();
    }

    @Override
    public Storeable get(String key) {
        if (!threadLocal.get().data.equals(data)) {
            threadLocal.get().data = data;
        }
        return threadLocal.get().get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (!threadLocal.get().data.equals(data)) {
            threadLocal.get().data = data;
        }
        return threadLocal.get().put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (!threadLocal.get().data.equals(data)) {
            threadLocal.get().data = data;
        }
        return threadLocal.get().remove(key);
    }

    @Override
    public int size() {
        if (!threadLocal.get().data.equals(data)) {
            threadLocal.get().data = data;
        }
        return threadLocal.get().size();
    }

    @Override
    public List<String> list() {
        if (!threadLocal.get().data.equals(data)) {
            threadLocal.get().data = data;
        }
        return threadLocal.get().list();
    }

    @Override
    public int commit() throws IOException {
        if (!threadLocal.get().data.equals(data)) {
            threadLocal.get().data = data;
        }
        int result = threadLocal.get().commit();
        data = threadLocal.get().data;
        return result;
    }

    @Override
    public int rollback() {
        return threadLocal.get().rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return threadLocal.get().getNumberOfUncommittedChanges();
    }

    @Override
    public int getColumnsCount() {
        return threadLocal.get().getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return threadLocal.get().getColumnType(columnIndex);
    }

    public void close() {
        threadLocal.get().close();
    }

    public MyTable getMultiTable() {
        return threadLocal.get();
    }

    public void handle(String[] args) throws IOException {
        switch (args[0]) {
            case "commit":
                System.out.println(commit());
                break;
            case "size":
                System.out.println(size());
                break;
            case "rollback":
                System.out.println(rollback());
                break;
            default:
                threadLocal.get().handle(args);
        }
    }
}
