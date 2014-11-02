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

    private void load() {
        for (String tablename : path.toFile().list()) {
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
            throw new IllegalArgumentException("the database directory wasn't specified");
        }
        try {
            currentTable = null;
            database = new HashMap<String, Table>();
            path = Paths.get(dir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            } else if (Files.isDirectory(path)) {
                load();
            } else {
                throw new IllegalArgumentException(dir + ": is a normal file");
            }
        } catch (IOException e) {
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
        if (name == null) {
            throw new IllegalArgumentException("invalid tablename");
        } else if (database.containsKey(name)) {
            throw new IllegalArgumentException("tablename exists");
        }
        return database.put(name, new Table(path.resolve(name)));
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("invalid tablename");
        } else if (!database.containsKey(name)) {
            throw new IllegalStateException("tablename not exist");
        } else if (name.equals(currentTable)) {
            currentTable = null;
        }
        Shell.removeDir(path.resolve(name));
        database.remove(name);
    }

    public String useTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("invalid tablename");
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
