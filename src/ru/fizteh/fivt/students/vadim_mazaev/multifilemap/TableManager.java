package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class TableManager {
    private Map<String, Table> tableLinks;
    private Path tablesDirPath;
    private Table usedTable;
    
    public TableManager(String dir) throws IllegalArgumentException {
        usedTable = null;
        try {
            tablesDirPath = Paths.get(dir);
            if (!tablesDirPath.toFile().exists()) {
                tablesDirPath.toFile().mkdir();
            }
            if (!tablesDirPath.toFile().isDirectory()) {
                throw new IllegalArgumentException("Error connecting database"
                        + ": path is incorrect or does not lead to a directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Error connecting database"
                    + ": illegal directory name", e);
        }
        tableLinks = new TreeMap<>();
        String[] tablesDirlist = tablesDirPath.toFile().list();
        for (String curTableDir : tablesDirlist) {
            Path curTableDirPath = tablesDirPath.resolve(curTableDir);
            if (curTableDirPath.toFile().isDirectory()) {
                Table curTable = new Table(curTableDirPath, curTableDir);
                tableLinks.put(curTableDir, curTable);
            } else {
                throw new IllegalArgumentException("Error connecting database"
                        + ": root directory contains non-directory files");
            }
        }
    }
    
    public Table getUsedTable() {
        return usedTable;
    }
    
    public boolean useTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            tablesDirPath.resolve(name);
            if (name.matches("[\\/\\.]+")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            Table newTable = tableLinks.get(name);
            if (newTable != null) {
                if (usedTable != null) {
                    usedTable.commit();
                }
                usedTable = newTable;
                return true;
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name", e);
        }
        return false;
    }
    
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            tablesDirPath.resolve(name);
            if (name.matches("[\\/\\.]+")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            return tableLinks.get(name);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name", e);
        }
    }

    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            Path newTablePath = tablesDirPath.resolve(name);
            if (name.matches("[\\/\\.]+")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            if (tableLinks.get(name) != null) {
                return null;
            }
            newTablePath.toFile().mkdir();
            Table newTable = new Table(newTablePath, name);
            tableLinks.put(name, newTable);
            return newTable;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name", e);
        }
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            tablesDirPath.resolve(name);
            if (name.matches("[\\/\\.]+")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            Table removedTable = tableLinks.remove(name);
            if (removedTable == null) {
                throw new IllegalStateException("There is no such table");
            } else {
                if (usedTable == removedTable) {
                    usedTable = null;
                }
                removedTable.deleteTable();
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name", e);
        } catch (IOException e) {
            throw new IllegalStateException("Table can't be removed from disk", e);
        }
    }
    
    public Set<String> getTablesSet() {
        return tableLinks.keySet();
    }
}
