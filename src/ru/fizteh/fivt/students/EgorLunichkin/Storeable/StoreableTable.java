package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;
import java.util.List;

public class StoreableTable implements Table {
    public String getName() {
        return null;
    }

    public Storeable get(String key) {
        return null;
    }

    public Storeable put(String key, Storeable value) {
        return null;
    }

    public Storeable remove(String key) {
        return null;
    }

    public int size() {
        return 0;
    }

    public List<String> list() {
        return null;
    }

    public int commit() {
        return 0;
    }

    public int rollback() {
        return 0;
    }

    public int getNumberOfUncommittedChanges() {
        return 0;
    }

    public int getColumnsCount() {
        return  0;
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return null;
    }
}
