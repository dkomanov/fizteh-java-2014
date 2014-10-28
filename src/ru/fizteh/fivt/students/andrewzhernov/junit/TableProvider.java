package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

public class TableProvider {
    private Path path;
    private Map<String, Table> database;
    private String currentTable;
    private String commandOutput;

    private void load() throws Exception {
        for (String tablename : path.toFile().list()) {
            database.put(tablename, new Table(path.resolve(tablename)));
        }
    }

    public TableProvider(String dir) throws Exception {
        if (dir == null) {
            throw new Exception("The database directory wasn't specified");
        }
        commandOutput = null;
        currentTable = null;
        database = new HashMap<String, Table>();
        path = Paths.get(dir);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        } else if (Files.isDirectory(path)) {
            load();
        } else {
            throw new Exception(dir + ": is a normal file");
        }
    }

    public void printCommandOutput() throws Exception {
        if (commandOutput != null) {
            System.out.println(commandOutput);
            commandOutput = null;
        }
    }

    public Table getTable(String name) throws Exception {
        commandOutput = null;
        return database.get(name);
    }

    public Table getCurrentTable() throws Exception {
        commandOutput = null;
        if (currentTable == null) {
            throw new Exception("no table");
        }
        return database.get(currentTable);
    }

    public Table createTable(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Invalid tablename");
        } else if (!database.containsKey(name)) {
            commandOutput = "created";
            return database.put(name, new Table(path.resolve(name)));
        }
        commandOutput = "tablename exists";
        return null;
    }

    public void removeTable(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Invalid tablename");
        } else if (!database.containsKey(name)) {
            throw new IllegalStateException("tablename not exist");
        } else {
            commandOutput = "dropped";
            Command.remove(path.resolve(name));
            currentTable = null;
            database.remove(name);
        }
    }

    public void useTable(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Invalid tablename");
        } else if (!database.containsKey(name)) {
            throw new IllegalStateException("tablename not exist");
        } else if (currentTable != null) {
            int count = getCurrentTable().unsavedSize();
            if (count > 0) {
                throw new Exception(Integer.toString(count) + " unsaved changes");
            }
        }
        currentTable = name;
        commandOutput = "using tablename";
    }

    public void showTables() throws Exception {
        commandOutput = null;
        for (String tablename : database.keySet()) {
            System.out.printf("%s %d\n", tablename, getTable(tablename).size());
        }
    }

    public void exit() throws Exception {
        commandOutput = null;
        if (currentTable != null) {
            int count = getCurrentTable().unsavedSize();
            if (count > 0) {
                throw new Exception(Integer.toString(count) + " unsaved changes");
            }
        }
        System.exit(0);
    }
}
