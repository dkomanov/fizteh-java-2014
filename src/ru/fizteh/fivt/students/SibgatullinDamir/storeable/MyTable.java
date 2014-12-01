package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Lenovo on 09.11.2014.
 */
public class MyTable implements Table {

    Path location;
    String name;
    FileMap currentTable;
    FileMap committedTable;
    List<Class<?>> types;
    MyTableProvider provider;
    Set<String> changedKeys = new LinkedHashSet<>();

    protected MyTable(String passedName, FileMap passedTable, List<Class<?>> passedTypes,
                      MyTableProvider passedProvider, Path passedDirectory) {
        name = passedName;
        currentTable = passedTable;
        committedTable = new FileMap(name);
        committedTable.putAll(passedTable);
        types = passedTypes;
        provider = passedProvider;
        location = passedDirectory;
    }

    public String getName() {
        return name;
    }

    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String result = currentTable.get(key);
        try {
            return provider.deserialize(this, result);
        } catch (ParseException e) {
            return null;
        }
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        try {
            String result = currentTable.put(key, provider.serialize(this, value));
            changedKeys.add(key);
            if (result == null) {
                return null;
            }
            return provider.deserialize(this, result);
        } catch (ParseException e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        try {
            if (changedKeys.contains(key)) {
                changedKeys.remove(key);
            } else {
                changedKeys.add(key);
            }
            String result = currentTable.remove(key);
            return provider.deserialize(this, result);
        } catch (Exception e) {
            return null;
        }
    }

    public int size() {
        return currentTable.size();
    }

    public int commit() throws IOException {
        try {
            currentTable.write(location);
        } catch (MyException e) {
            throw new IOException();
        }
        committedTable.clear();
        committedTable.putAll(currentTable);
        int countOfChanges = changedKeys.size();
        changedKeys.clear();
        return countOfChanges;
    }

    public int rollback() {
        currentTable.clear();
        currentTable.putAll(committedTable);
        int countOfChanges = changedKeys.size();
        changedKeys.clear();
        return countOfChanges;
    }

    public List<String> list() {
        List<String> result = new LinkedList<>();
        for (String key: currentTable.keySet()) {
            result.add(key);
        }
        return result;
    }

    public int getColumnsCount() {
        return types.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
    }

    public int getNumberOfUncommittedChanges() {
        return changedKeys.size();
    }
}
