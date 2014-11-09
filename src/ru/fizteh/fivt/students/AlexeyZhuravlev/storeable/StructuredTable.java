package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.HybridTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTable;

import java.io.IOException;
import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class StructuredTable implements Table {

    MyTable table;
    List<Class<?>> types;
    StructuredTableProvider provider;

    protected StructuredTable(HybridTable hybridTable, List<Class<?>> passedTypes, String name,
                              StructuredTableProvider passedProvider) {
        table = new MyTable(hybridTable, name);
        types = passedTypes;
        provider = passedProvider;
    }

    @Override
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
