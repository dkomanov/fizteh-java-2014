package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public final class TableProvider {
    private static Map<String, Table> tableLinks;
    private static Path tablesDirPath;
    private static Table currentTable;

    public TableProvider(final String dir) {
        tablesDirPath = Paths.get(dir);
        if (!Files.exists(tablesDirPath)) {
            tablesDirPath.toFile().mkdir();
        }
        if (!tablesDirPath.toFile().isDirectory()) {
            throw new IllegalArgumentException("Incorrect path.");
        }
        currentTable = null;
        tableLinks = new HashMap<>();
        String[] tablesDirlist = tablesDirPath.toFile().list();
        for (String curTableDir : tablesDirlist) {
            Path curTableDirPath = tablesDirPath.resolve(curTableDir);
            if (curTableDirPath.toFile().isDirectory()) {
                Table curTable = new Table(curTableDirPath, curTableDir);
                tableLinks.put(curTableDir, curTable);
            } else {
                throw new IllegalArgumentException(
                        "Directory contains non-directory files.");
            }
        }
    }

    public static void showTables() {
        Set<String> keys = tableLinks.keySet();
        for (String current : keys) {
            System.out.println(current + " " + tableLinks.get(current).size());
        }
    }

    public static void changeCurTable(final String name) throws IOException {
        try {
            tablesDirPath.resolve(name);
            Table newTable = tableLinks.get(name);
            if (newTable != null) {
                if (currentTable != null) {
                    currentTable.commit();
                }
                currentTable = newTable;
                System.out.println("using " + name);
            } else {
                System.out.println(name + " does not exist");
            }
        } catch (InvalidPathException e) {
            System.err.println("table name " + name + " is incorrect. "
                    + e.getMessage());
            return;
        }
    }

    public static void createTable(final String name) {
        try {
            Path newTablePath = tablesDirPath.resolve(name);
            if (Files.exists(tablesDirPath)) {
                if (tableLinks.get(name) != null) {
                    System.out.println(name + " exists");
                    return;
                }
                newTablePath.toFile().mkdir();
                Table newTable = new Table(newTablePath, name);
                tableLinks.put(name, newTable);
                System.out.println("created");
            } else {
                throw new IllegalArgumentException("Incorrect name.");
            }
        } catch (InvalidPathException e) {
            System.err.println("table name " + name + " is incorrect. "
                    + e.getMessage());
            return;
        }
    }

    public static void removeTable(final String name) throws IOException {
        try {
            tablesDirPath.resolve(name);
            Table removedTable = tableLinks.remove(name);
            if (removedTable == null) {
                System.out.println(name + " does not exist");
            } else {
                if (currentTable == removedTable) {
                    currentTable = null;
                }
                removedTable.deleteTable();
                System.out.println("dropped");
            }
        } catch (InvalidPathException e) {
            System.err.println("table name " + name + " is incorrect. "
                    + e.getMessage());
            return;
        }
    }

    public Table getDataBase() {
        return currentTable;
    }

}
