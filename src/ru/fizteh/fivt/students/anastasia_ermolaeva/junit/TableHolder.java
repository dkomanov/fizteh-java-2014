package ru.fizteh.fivt.students.anastasia_ermolaeva.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseIOException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TableHolder implements TableProvider {
    private Path rootPath;
    private Map<String, DBTable> tableMap;

    public TableHolder(final String rootDir) {
        try {
            rootPath = Paths.get(rootDir);
            if (!rootPath.toFile().exists()) {
                try {
                    Files.createDirectory(rootPath);
                } catch (IOException e) {
                    throw new DatabaseIOException(e.getMessage());
                }
            }
            if (!rootPath.toFile().isDirectory()) {
                throw new IllegalArgumentException(rootDir
                        + " is not directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(rootDir
                    + "' is illegal directory name", e);
        }
        tableMap = new HashMap<>();
        Utility.checkDirectorySubdirs(rootPath);
        try {
            for (File currentSubdir : rootPath.toFile().listFiles()) {
                String tableName = currentSubdir.getName();
                tableMap.put(tableName, new DBTable(rootPath, tableName));
            }
        } catch (NullPointerException n) {
            throw new DatabaseIOException("Access forbidden");
        }
    }

    public final Map<String, DBTable> getTableMap() {
        return tableMap;
    }

    public final void close() {
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            entry.getValue().commit();
        }
        tableMap.clear();
    }

    @Override
    public final Table getTable(final String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            return tableMap.get(tableName);
        } else {
            return null;
        }
    }

    @Override
    public final Table createTable(final String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            return null;
        } else {
            Path pathTableDirectory = rootPath.resolve(tableName);
            try {
                Files.createDirectory(pathTableDirectory);
                DBTable newTable = new DBTable(rootPath,
                        tableName, new HashMap<>());
                tableMap.put(tableName, newTable);
                return newTable;
            } catch (IOException e) {
                throw new DatabaseIOException("Can't create table directory");
            }
        }
    }

    @Override
    public final void removeTable(final String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (!tableMap.containsKey(tableName)) {
            throw new IllegalStateException(tableName + " doesn't exist");
        } else {
            Path tableDirectory = tableMap.get(tableName).getDBTablePath();
            Utility.recursiveDelete(tableDirectory);
            tableMap.remove(tableName);
        }
    }
}
