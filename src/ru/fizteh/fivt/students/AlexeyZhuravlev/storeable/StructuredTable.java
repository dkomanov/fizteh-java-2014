package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.HybridTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTable;

import java.io.IOException;
import java.text.ParseException;
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
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        try {
            String old = table.put(key, provider.serialize(this, value));
            if (old == null) {
                return null;
            }
            return provider.deserialize(this, old);
        } catch (ParseException e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String old = table.remove(key);
        if (old == null) {
            return null;
        }
        try {
            return provider.deserialize(this, old);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public int commit() throws IOException {
        return table.commit();
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
    }

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String old = table.get(key);
        if (old == null) {
            return null;
        }
        try {
            return provider.deserialize(this, old);
        } catch (ParseException e) {
            return null;
        }
    }
}
