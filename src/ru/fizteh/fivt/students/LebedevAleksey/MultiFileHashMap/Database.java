package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Database {
    private static final String CANT_CREATE_TABLE = "Can't create table";
    private static final String CANT_CREATE_TABLE_MESSAGE = CANT_CREATE_TABLE + ": ";
    private static final String INCORRECT_NAME_OF_TABLES = "This name is not correct, folder can't be created";
    private File rootDirectory;
    private Table currentTable;
    private List<Table> tables = new ArrayList<>();
    private Map<String, Table> tableNames = new TreeMap<>();

    public Database(String path) throws DatabaseFileStructureException, LoadOrSaveException {
        setRootDirectoryPath(path);
        load();
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    void setCurrentTable(String name) throws TableNotFoundException {
        currentTable = getTable(name);
    }

    protected File getRootDirectory() throws DatabaseFileStructureException {
        return rootDirectory;
    }

    protected Path getRootDirectoryPath() throws DatabaseFileStructureException {
        return getRootDirectory().toPath();
    }

    private void setRootDirectoryPath(String directoryPath) throws DatabaseFileStructureException {
        rootDirectory = new File(directoryPath);
    }

    protected void load() throws LoadOrSaveException, DatabaseFileStructureException {
        File root = getRootDirectory();
        try {
            if (root.exists() && root.isDirectory()) {
                File[] subfolders = getTablesFromRoot(root);
                for (File folder : subfolders) {
                    String name = folder.getName();
                    Table table = generateTable(name);
                    tables.add(table);
                    tableNames.put(name, table);
                }
            } else {
                throw new DatabaseFileStructureException("Root directory not found");
            }
        } catch (SecurityException ex) {
            throw new LoadOrSaveException("Error in loading, access denied: " + ex.getMessage(), ex);
        }
    }

    protected File[] getTablesFromRoot(File root) throws DatabaseFileStructureException {
        File[] subfolders = root.listFiles();
        for (File folder : subfolders) {
            if (!folder.isDirectory()) {
                return whatToDoWithFiles(folder);
            }
        }
        return subfolders;
    }

    private File[] whatToDoWithFiles(File folder) throws DatabaseFileStructureException {
        throw new DatabaseFileStructureException("There is files in root folder. File'" + folder.getName() + "'");
    }

    public List<Pair<String, Integer>> listTables() throws LoadOrSaveException, DatabaseFileStructureException {
        int size = tables.size();
        List<Pair<String, Integer>> result = new ArrayList<>(size);
        for (Table table : tables) {
            result.add(new Pair<>(table.getTableName(), table.count()));
        }
        return result;
    }

    public Table getTable(String name) throws TableNotFoundException {
        Table table = tableNames.get(name);
        if (table == null) {
            throw new TableNotFoundException();
        } else {
            return table;
        }
    }

    public boolean containsTable(String name) {
        return tableNames.containsKey(name);
    }


    public void useTable(String name) throws TableNotFoundException, LoadOrSaveException,
            DatabaseFileStructureException {
        if (currentTable != null) {
            currentTable.save();
        }
        setCurrentTable(name);
    }

    public Table createTable(String name) throws TableAlreadyExistsException, LoadOrSaveException,
            DatabaseFileStructureException {
        try {
            if (!containsTable(name)) {
                Table table = generateTable(name);
                Path rootDirectoryPath = getRootDirectoryPath();
                Path path = rootDirectoryPath.resolve(name);
                if (path.startsWith(rootDirectoryPath) && path.getParent().equals(rootDirectoryPath)) {
                    Files.createDirectory(path);
                    tables.add(table);
                    tableNames.put(name, table);
                    return table;
                } else {
                    throw new DatabaseFileStructureException(INCORRECT_NAME_OF_TABLES);
                }
            } else {
                throw new TableAlreadyExistsException();
            }
        } catch (UnsupportedOperationException ex) {
            throw new LoadOrSaveException(CANT_CREATE_TABLE_MESSAGE, ex);
        } catch (FileAlreadyExistsException ex) {
            throw new DatabaseFileStructureException(CANT_CREATE_TABLE + ", directory already exists", ex);
        } catch (InvalidPathException ex) {
            throw new DatabaseFileStructureException(
                    CANT_CREATE_TABLE + ", directory name makes it impossible to create a directory", ex);
        } catch (IOException | SecurityException ex) {
            throw new LoadOrSaveException(CANT_CREATE_TABLE_MESSAGE, ex);
        }
    }

    protected Table generateTable(String name) {
        return new Table(name, this);
    }

    public void removeTable(String name) throws TableNotFoundException, LoadOrSaveException,
            DatabaseFileStructureException {
        Table table = getTable(name);
        if (table == currentTable) {
            currentTable = null;
        }
        table.drop();
        tables.remove(table);
        tableNames.remove(name);
    }
}
