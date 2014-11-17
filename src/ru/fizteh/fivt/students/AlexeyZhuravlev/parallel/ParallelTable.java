package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class ParallelTable implements Table {

    StructuredTable originalTable;
    ThreadLocal<Diff> diff;
    ParallelTableProvider provider;

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        provider.serialize(originalTable, value);
        return diff.get().put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        return diff.get().remove(key);
    }

    @Override
    public int size() {
        // *** обращение к таблице
        int originalSize = originalTable.size();
        // ***
        return originalSize + diff.get().deltaSize();
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
        return diff.get().get(key);
    }
}
