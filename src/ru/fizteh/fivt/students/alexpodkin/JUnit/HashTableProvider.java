package ru.fizteh.fivt.students.alexpodkin.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alex on 11.11.14.
 */
public class HashTableProvider implements TableProvider {

    private String dirPath;
    private Set<String> tableNames;

    public HashTableProvider(String path) {
        dirPath = path;
        tableNames = new HashSet<>();
        for (File file : new File(path).listFiles()) {
            if (file.isDirectory()) {
                HashTable table = new HashTable(file.getName(), dirPath);
                tableNames.add(file.getName());
            }
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name shouldn't be null");
        }
        if (!tableNames.contains(name)) {
            return null;
        }
        return new HashTable(name, dirPath);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name shouldn't be null");
        }
        if (tableNames.contains(name)) {
            return null;
        }
        tableNames.add(name);
        File newTable = new File(dirPath + File.separator + name);
        if (!newTable.mkdir()) {
            throw new IllegalArgumentException("Can't create new table");
        }
        return new HashTable(name, dirPath);
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name shouldn't be null");
        }
        if (!tableNames.contains(name)) {
            throw new IllegalArgumentException("Table" + name + "doesn't exist");
        }
        tableNames.remove(name);
        new HashTable(name, dirPath).removeData();
    }
}
