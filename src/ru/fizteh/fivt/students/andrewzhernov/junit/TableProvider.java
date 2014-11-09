package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

public class TableProvider implements TableProviderInterface {
    private Path path;
    private Map<String, Table> database;
    private String currentTable;

    private void load() throws Exception {
        for (String tablename : path.toFile().list()) {
            if (tablename.indexOf('/') != -1) {
                throw new Exception(tablename + ": incorrect tablename");
            }
            database.put(tablename, new Table(path.resolve(tablename)));
        }
    }

    private void checkUnsavedChanges() throws IllegalStateException {
        int count = getCurrentTable().unsavedSize();
        if (count > 0) {
            throw new IllegalStateException(Integer.toString(count) + " unsaved changes");
        }
    }

    public TableProvider(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("The database directory wasn't specified");
        }
        currentTable = null;
        database = new HashMap<String, Table>();
        path = Paths.get(dir);
        try {
            if (Files.notExists(path)) {
                if (!path.toFile().getCanonicalFile().getParentFile().canWrite()) {
                    throw new Exception(path.toString() + ": don't have permission to create the directory");
                }
                Files.createDirectory(path);
            } else if (!path.toFile().canRead()) {
                throw new Exception(path.toString() + ": don't have permission to read the directory");
            } else if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException(dir + ": isn't a directory");
            } else {
                load();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("no table");
        }
        return database.get(name);
    }

    public Table getCurrentTable() throws IllegalArgumentException {
        return getTable(currentTable);
    }

    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null || name.indexOf('/') != -1) {
            throw new IllegalArgumentException(name + ": incorrect tablename");
        } else if (database.containsKey(name)) {
            throw new IllegalArgumentException("tablename exists");
        }
        Table newTable = new Table(path.resolve(name));
        database.put(name, newTable);
        return newTable;
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.indexOf('/') != -1) {
            throw new IllegalArgumentException(name + ": incorrect tablename");
        } else if (!database.containsKey(name)) {
            throw new IllegalStateException("tablename not exist");
        } else if (name.equals(currentTable)) {
            currentTable = null;
        }
        Utils.removeDir(path.resolve(name));
        database.remove(name);
    }

    public String useTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.indexOf('/') != -1) {
            throw new IllegalArgumentException(name + ": incorrect tablename");
        } else if (!database.containsKey(name)) {
            throw new IllegalArgumentException("tablename not exist");
        } else if (currentTable != null && !name.equals(currentTable)) {
            checkUnsavedChanges();
        }
        currentTable = name;
        return currentTable;
    }

    public Map<String, Integer> showTables() {
        Map<String, Integer> tables = new HashMap<>();
        for (String tablename : database.keySet()) {
            tables.put(tablename, database.get(tablename).size());
        }
        return tables;
    }

    public void exit() throws IllegalStateException {
        if (currentTable != null) {
            checkUnsavedChanges();
        }
    }
}
