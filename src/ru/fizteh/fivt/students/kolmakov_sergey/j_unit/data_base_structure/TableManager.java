package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.DatabaseCorruptedException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class TableManager implements TableProvider {
    private Map<String, Table> tableManagerMap;
    private Table currentTable;
    private final Path databasePath;
    public static final String CODE_FORMAT = "UTF-8";
    protected static final int NUMBER_OF_PARTITIONS = 16;
    protected static final String FOLDER_NAME_PATTERN = "([0-9]|1[0-5])\\.dir";
    protected static final String FILE_NAME_PATTERN = "([0-9]|1[0-5])\\.dat";
    private static final String ILLEGAL_TABLE_NAME_PATTERN = ".*\\.|\\..*|.*(/|\\\\).*";

    public TableManager(String path) throws IllegalArgumentException {
        currentTable = null;
        databasePath = Paths.get(path);
        tableManagerMap = new HashMap<>();
        if (!databasePath.toFile().exists()) {
            databasePath.toFile().mkdir();
        } else if (!databasePath.toFile().isDirectory()) {
            throw new IllegalArgumentException(path + ": is not a directory");
        }
        String[] tablesNames = databasePath.toFile().list();
        for (String currentTableName : tablesNames) {
            Path currentTablePath = databasePath.resolve(currentTableName);
            if (currentTablePath.toFile().isDirectory()) {
                TableClass curTable;
                try {
                    curTable = new TableClass(currentTablePath, currentTableName);
                } catch (DatabaseCorruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                tableManagerMap.put(currentTableName, curTable);
            } else {
                throw new IllegalArgumentException("Database corrupted: unexpected files in root directory");
            }
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("getTable: null argument");
        }
        if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("getTable: wrong name of table");
        }
        return tableManagerMap.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("createTable: null argument");
        }
        if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("createTable: wrong name of table");
        }
        Path newTablePath = databasePath.resolve(name);
        if (tableManagerMap.get(name) != null) {
            return null;
        }
        newTablePath.toFile().mkdir();
        try {
            TableClass newTable = new TableClass(newTablePath, name);
            tableManagerMap.put(name, newTable);
            return newTable;
        } catch (DatabaseCorruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("removeTable: wrong name of table");
        }
        Path tableDir = databasePath.resolve(name);
        Table removedTable = tableManagerMap.remove(name);
        if (removedTable == null) {
            throw new IllegalStateException("Table not found");
        } else {
            if (currentTable == removedTable) {
                currentTable = null;
            }
            deleteRecursively(tableDir.toFile());
        }
    }

    protected static int excludeFolderNumber(String folderName) {
        return Integer.parseInt(folderName.substring(0, folderName.length() - 4));
    }
    protected static int excludeDataFileNumber(String fileName) {
        return Integer.parseInt(fileName.substring(0, fileName.length() - 4));
    }

    public Set<String> getNames() {
        return tableManagerMap.keySet();
    }


    private static void deleteRecursively(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    deleteRecursively(currentFile);
                }
            } catch (NullPointerException e) {
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}

class Coordinates implements Comparable<Coordinates> {
    protected final int folderIndex;
    protected final int fileIndex;
    public Coordinates(int folderIndex, int fileIndex) {
        this.folderIndex = folderIndex;
        this.fileIndex = fileIndex;
    }
    public Coordinates(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Error: key == null");
        }
        try {
            folderIndex = Math.abs(key.getBytes(TableManager.CODE_FORMAT)[0] % TableManager.NUMBER_OF_PARTITIONS);
            fileIndex = Math.abs((key.getBytes(TableManager.CODE_FORMAT)[0] / TableManager.NUMBER_OF_PARTITIONS)
                    % TableManager.NUMBER_OF_PARTITIONS);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Bad key is given to constructor of Coordinates");
        }
    }
    @Override
    public int compareTo(Coordinates coordinates) { // Not used in current version, but can be useful.
        if (folderIndex > coordinates.folderIndex) {
            return 1;
        } else if (folderIndex < coordinates.folderIndex) {
            return  -1;
        } else if (fileIndex > coordinates.fileIndex) {
            return  1;
        } else if (fileIndex < coordinates.fileIndex) {
            return  -1;
        }
        return 0;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Coordinates)) {
            return  false;
        } else {
            Coordinates coordinates = (Coordinates) o;
            return folderIndex == coordinates.folderIndex && fileIndex == coordinates.fileIndex;
        }
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + fileIndex;
        hash = 53 * hash + folderIndex;
        return hash;
    }
}
