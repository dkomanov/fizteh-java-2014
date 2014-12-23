package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.EgorLunichkin.JUnit.MyTable;

import java.text.ParseException;
import java.util.List;

public class StoreableTable implements Table {
    public StoreableTable(MyTable givenTable, List<Class<?>> givenTypes, StoreableTableProvider givenTableProvider) {
        table = givenTable;
        types = givenTypes;
        tableProvider = givenTableProvider;
    }

    private MyTable table;
    public List<Class<?>> types;
    private StoreableTableProvider tableProvider;

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = table.get(key);
        if (oldValue == null) {
            return null;
        }
        try {
            return tableProvider.deserialize(this, oldValue);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        try {
            String oldValue = table.put(key, tableProvider.serialize(this, value));
            if (oldValue == null) {
                return null;
            }
            return tableProvider.deserialize(this, oldValue);
        } catch (ParseException ex) {
            throw new ColumnFormatException(ex.getMessage());
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = table.remove(key);
        if (oldValue == null) {
            return null;
        }
        try {
            return tableProvider.deserialize(this, oldValue);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public List<String> list() {
        return table.list();
    }

    @Override
    public int commit() {
        return table.commit();
    }

    @Override
    public int rollback() {
        return table.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return table.unsavedChanges();
    }

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
    }

    public TableProvider getProvider() {
        return tableProvider;
    }
}
