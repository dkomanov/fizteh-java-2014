package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TableManager {
    private Map<String, Table> tableManagerMap;
    private Table currentTable;
    private final Path databasePath;
    protected static final int MAGIC_NUMBER = 16;
    protected static final String FOLDER_NAME_PATTERN = "([0-9]|1[0-5])\\.dir";
    protected static final String FILE_NAME_PATTERN = "([0-9]|1[0-5])\\.dat";
    private static final String ILLEGAL_TABLE_NAME_PATTERN = ".*\\.|\\..*|.*(/|\\\\).*";

    public TableManager(String path) throws IllegalArgumentException {
        currentTable = null;
        databasePath = Paths.get(path);
        tableManagerMap = new TreeMap<>();
        if (!databasePath.toFile().exists()) {
            databasePath.toFile().mkdir();
            System.out.print("Can't find data base folder.\nDirectory "
                    + databasePath.toString() + " was created\n");
        } else if (!databasePath.toFile().isDirectory()) {
            throw new IllegalArgumentException(path + ": is not a directory");
        }
        String[] tablesNames = databasePath.toFile().list();
        for (String currentTableName : tablesNames) {
            Path currentTablePath = databasePath.resolve(currentTableName);
            if (currentTablePath.toFile().isDirectory()) {
                Table curTable = new Table(currentTablePath, currentTableName);
                tableManagerMap.put(currentTableName, curTable);
            } else {
                throw new IllegalArgumentException("Database corrupted: unexpected files in root directory");
            }
        }
    }

    protected Table getCurrentTable() {
        return currentTable;
    }

    protected boolean useTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("UseTable: null argument");
        }
        Table newTable = tableManagerMap.get(name);
        if (newTable != null) {
            if (currentTable != null) {
                currentTable.saveData();
            }
            currentTable = newTable;
            return true;
        } else {
            return false;
        }
    }

    protected int getTableSize(String name) {
        if (name == null) {
            throw new IllegalArgumentException("getTableSize: null argument");
        }
        if (tableManagerMap.get((name)) == null) {
            throw new IllegalArgumentException("No such Table found");
        }
        return tableManagerMap.get(name).size();
    }

    protected Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("createTable: null argument");
        }
        Path newTablePath = databasePath.resolve(name);
        if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("Illegal table name");
        }
        if (tableManagerMap.get(name) != null) {
            return null;
        }
        newTablePath.toFile().mkdir();
        Table newTable = new Table(newTablePath, name);
        tableManagerMap.put(name, newTable);
        return newTable;
    }

    protected void dropTable(String name) throws IllegalStateException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("Illegal table name");
        }
        Table removedTable = tableManagerMap.remove(name);
        if (removedTable == null) {
            throw new IllegalStateException(name + " not exists");
        } else {
            if (currentTable == removedTable) {
                currentTable = null;
            }
            removedTable.drop();
        }
    }

    protected static int excludeFolderNumber(String folderName) {
        return Integer.parseInt(folderName.substring(0, folderName.length() - 4));
    }
    protected static int excludeDataFileNumber(String fileName) {
        return Integer.parseInt(fileName.substring(0, fileName.length() - 4));
    }

    protected Set<String> getNames() {
        tableManagerMap.keySet();
        return tableManagerMap.keySet();
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
            folderIndex = Math.abs(key.getBytes("UTF-8")[0] % TableManager.MAGIC_NUMBER);
            fileIndex = Math.abs((key.getBytes("UTF-8")[0] / TableManager.MAGIC_NUMBER) % TableManager.MAGIC_NUMBER);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Encoding exception if function getHash");
        }
    }
    @Override
    public int compareTo(Coordinates pair) {
        if (folderIndex > pair.folderIndex){
            return 1;
        } else if (folderIndex < pair.folderIndex) {
            return  -1;
        } else if (fileIndex > pair.fileIndex) {
            return  1;
        } else if (fileIndex < pair.fileIndex) {
            return  -1;
        }
        return 0;
    }
}
