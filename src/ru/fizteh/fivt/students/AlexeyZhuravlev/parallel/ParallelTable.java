package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class ParallelTable implements Table {

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable remove(String key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int commit() throws IOException {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return 0;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Storeable get(String key) {
        return null;
    }
}
