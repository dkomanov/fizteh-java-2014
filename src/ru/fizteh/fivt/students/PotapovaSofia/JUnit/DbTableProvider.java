package ru.fizteh.fivt.students.PotapovaSofia.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DbTableProvider implements TableProvider {
    private Map<String, Table> tables;
    private Path dbPath;

    public DbTableProvider(String dir) {
        try {
            dbPath = Paths.get(dir);
            if (!dbPath.toFile().exists()) {
                dbPath.toFile().mkdir();
            }
            if (!dbPath.toFile().isDirectory()) {
                throw new IllegalArgumentException("Error connecting database"
                        + ": path is incorrect or does not lead to a directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Error connecting database"
                    + ": '" + dir + "' is illegal directory name", e);
        }
        tables = new TreeMap<>();
        String[] tablesDirlist = dbPath.toFile().list();
        for (String curTableDir : tablesDirlist) {
            Path curTableDirPath = dbPath.resolve(curTableDir);
            if (curTableDirPath.toFile().isDirectory()) {
                Table curTable = new DbTable(curTableDirPath, curTableDir);
                tables.put(curTableDir, curTable);
            } else {
                throw new IllegalArgumentException("Error connecting database"
                        + ": root directory contains non-directory files");
            }
        }
    }

    @Override
    public Table getTable(String name) {
        Utils.checkOnNull(name, "Table name is null");
        Utils.checkTableName(name);
        try {
            dbPath.resolve(name);
            return tables.get(name);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name: " + e.getMessage(), e);
        }
    }

    @Override
    public Table createTable(String name) {
        Utils.checkOnNull(name, "Table name is null");
        Utils.checkTableName(name);
        try {
            if (tables.get(name) != null) {
                return null;
            }
            Path newTablePath = dbPath.resolve(name);
            newTablePath.toFile().mkdir();
            Table newTable = new DbTable(newTablePath, name);
            tables.put(name, newTable);
            return newTable;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeTable(String name) {
        Utils.checkOnNull(name, "Table name is null");
        Utils.checkTableName(name);
        try {
            Path tableDir = dbPath.resolve(name);
            Table removedTable = tables.remove(name);
            if (removedTable == null) {
                throw new IllegalStateException("There is no such table");
            } else {
                recoursiveDelete(tableDir.toFile());
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Illegal table name: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Table can't be removed from disk: " + e.getMessage(), e);
        }
    }

    public Set<String> getTablesSet() {
        return tables.keySet();
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
