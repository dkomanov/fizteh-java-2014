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
    protected final static int magicNumber = 16;
    protected final static String folderNamePattern = "([0-9]|1[0-5])\\.dir";
    protected final static String fileNamePattern = "([0-9]|1[0-5])\\.dat";
    private final static String illegalTableNamePattern = ".*\\.|\\..*|.*(/|\\\\).*";

    public TableManager(String path) throws IllegalArgumentException {
        currentTable = null;
        databasePath = Paths.get(path);
        if (!databasePath.toFile().exists()) {
            databasePath.toFile().mkdir();
            System.out.print("Can't find data base folder.\nDirectory "
                    + databasePath.toString() + " was created\n");
        } else if (!databasePath.toFile().isDirectory()) {
            throw new IllegalArgumentException(path + ": is not a directory");
        }

        tableManagerMap = new TreeMap<>();
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
        if (tableManagerMap.get((name)) == null){
            throw new IllegalArgumentException("No such Table found");
        }
        return tableManagerMap.get(name).size();
    }

    protected Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("createTable: null argument");
        }
        Path newTablePath = databasePath.resolve(name);
        if (name.matches(illegalTableNamePattern)) {
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
        if (name.matches(illegalTableNamePattern)) {
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

    protected static int getHash (int fileIndex, int folderIndex) throws IllegalArgumentException {
        // Returns key that will be used in Table.tableMap.
        return fileIndex + folderIndex * magicNumber;
    }
    protected static int getHash (String key) throws IllegalArgumentException {
        // Returns key that will be used in Table.tableMap.
        if (key == null){
            throw new IllegalArgumentException("Error: key == null");
        }
        int folderIndex;
        int fileIndex;
        try {
            folderIndex = Math.abs(key.getBytes("UTF-8")[0] % magicNumber);
            fileIndex = Math.abs((key.getBytes("UTF-8")[0] / magicNumber) % magicNumber);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Encoding exception if function getHash");
        }
        return getHash(fileIndex, folderIndex);
    }

    protected static int excludeFolderNumber(String folderName){
        return Integer.parseInt(folderName.substring(0, folderName.length() - 4));
    }
    protected static int excludeDataFileNumber(String fileName){
        return Integer.parseInt(fileName.substring(0, fileName.length() - 4));
    }

    protected Set<String> getNames() {
        return tableManagerMap.keySet();
    }
}
