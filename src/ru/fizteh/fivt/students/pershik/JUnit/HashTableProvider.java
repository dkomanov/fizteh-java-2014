package ru.fizteh.fivt.students.pershik.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by pershik on 10/28/14.
 */
public class HashTableProvider implements TableProvider {

    private Set<String> tables;
    private String dbDirPath;

    HashTableProvider(String dbDir) {
        dbDirPath = dbDir;
        tables = new HashSet<>();
        initProvider();
    }

    @Override
    public HashTable getTable(String name) throws IllegalArgumentException {
        checkName(name);
        if (!tables.contains(name)) {
            return null;
        } else {
            return new HashTable(name, dbDirPath);
        }
    }

    @Override
    public HashTable createTable(String name) throws IllegalArgumentException {
        checkName(name);
        if (tables.contains(name)) {
            return null;
        } else {
            tables.add(name);
            String tableDirPath = dbDirPath + File.separator + name;
            File tableDir = new File(tableDirPath);
            if (!tableDir.mkdir()) {
                throw new IllegalArgumentException("Can't create this table");
            }
            return new HashTable(name, dbDirPath);
        }
    }

    @Override
    public void removeTable(String name)
            throws IllegalArgumentException, IllegalStateException {
        checkName(name);
        if (tables.contains(name)) {
            tables.remove(name);
            try {
                new HashTable(name, dbDirPath).removeFromDisk();
                File toRemoveDir = new File(dbDirPath + File.separator + name);
                if (!toRemoveDir.delete()) {
                    throw new IOException();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Can't remove table " + name);
            }
        } else {
            throw new IllegalStateException("Table " + name + " doesn't exists");
        }
    }

    public Map<String, Integer> getAllTables() {
        Map<String, Integer> res = new HashMap<>();
        for (String tableName : tables) {
            HashTable table = getTable(tableName);
            res.put(tableName, table.size());
        }
        return res;
    }

    private void checkName(String name) throws IllegalArgumentException {
        if (name == null || "..".equals(name) || ".".equals(name)
                || name.contains(File.separator)) {
            throw new IllegalArgumentException("Incorrect name");
        }
    }

    private void initProvider() {
        File dbDir = new File(dbDirPath);
        for (File curDir : dbDir.listFiles()) {
            if (curDir.isDirectory()) {
                HashTable table = new HashTable(curDir.getName(), dbDirPath);
                tables.add(curDir.getName());
            }
        }
    }
}
