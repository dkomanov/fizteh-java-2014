package ru.fizteh.fivt.students.VasilevKirill.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Storeable.junit.multimap.MultiTable;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kirill on 26.11.2014.
 */
public class SharedMyTable implements Table {
    private ThreadLocal<MultiTable> threadLocal;

    public SharedMyTable(MultiTable multiTable) throws IOException {
        threadLocal = new ThreadLocal<MultiTable>() {
            @Override
            protected MultiTable initialValue() {
                return multiTable;
            }
        };
        if (threadLocal == null) {
            throw new IOException("SharedTable: threadLocal wasn't initialized");
        }
    }

    @Override
    public String getName() {
        return threadLocal.get().getName();
    }

    @Override
    public Storeable get(String key) {
        return threadLocal.get().get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        return threadLocal.get().put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        return threadLocal.get().remove(key);
    }

    @Override
    public int size() {
        return threadLocal.get().size();
    }

    @Override
    public List<String> list() {
        return threadLocal.get().list();
    }

    @Override
    public int commit() throws IOException {
        return threadLocal.get().commit();
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

    public MultiTable getMultiTable() {
        return threadLocal.get();
    }
}
