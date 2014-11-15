package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public final class TableManager implements TableProvider {
    private Set<String> tableNames;
    private Path tablesDirPath;
    
    public TableManager(String dir) throws IllegalArgumentException {
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
                    + ": '" + dir + "' is illegal directory name", e);
        }
        tableNames = new HashSet<>();
        String[] tablesDirlist = tablesDirPath.toFile().list();
        for (String curTableDirName : tablesDirlist) {
            Path curTableDirPath = tablesDirPath.resolve(curTableDirName);
            if (curTableDirPath.toFile().isDirectory()) {
                //Construction of new Table is just for checking the directory.
                new DbTable(curTableDirPath, curTableDirName);
                tableNames.add(curTableDirName);
            } else {
                throw new IllegalArgumentException("Error connecting database"
                        + ": root directory contains non-directory files");
            }
        }
    }
    
    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            tablesDirPath.resolve(name);
            if (name.matches(".*[\\\\/\\.]+.*")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            if (tableNames.contains(name)) {
                return new DbTable(tablesDirPath.resolve(name), name);
            } else {
                return null;
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name: " + e.getMessage(), e);
        }
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            if (name.matches(".*[\\\\/\\.]+.*")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            if (tableNames.contains(name)) {
                return null;
            }
            Path newTablePath = tablesDirPath.resolve(name);
            newTablePath.toFile().mkdir();
            Table newTable = new DbTable(newTablePath, name);
            tableNames.add(name);
            return newTable;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        try {
            if (name.matches(".*[\\\\/\\.]+.*")) {
                throw new InvalidPathException(name, "contains '\',  or '/',  or '.'");
            }
            Path tableDir = tablesDirPath.resolve(name);
            if (!tableNames.contains(name)) {
                throw new IllegalStateException("There is no such table");
            } else {
                recoursiveDelete(tableDir.toFile());
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Table can't be removed from disk: "
                + e.getMessage(), e);
        }
    }
    
    public List<String> getTablesList() {
        List<String> namesList = new LinkedList<>();
        namesList.addAll(tableNames);
        return namesList;
    }
    
    private void recoursiveDelete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File currentFile : file.listFiles()) {
                recoursiveDelete(currentFile);
            }
        }
        if (!file.delete()) {
          throw new IOException("Unable to delete: " + file);
        }
    }
}
