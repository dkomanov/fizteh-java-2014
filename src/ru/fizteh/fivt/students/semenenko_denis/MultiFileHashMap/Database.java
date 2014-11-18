package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

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

import javafx.util.Pair;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Database implements TableProvider {
    private static final String CANT_CREATE_TABLE = "Can't create table";
    private static final String CANT_CREATE_TABLE_MESSAGE = CANT_CREATE_TABLE + ": ";
    private static final String INCORRECT_NAME_OF_TABLES = "This name is not correct, folder can't be created";
    private File rootDirectory;
    private Table usingTable;
    private List<TableHash> tables = new ArrayList<>();
    private Map<String, Table> tableNames = new TreeMap<>();

    public Database(String path) {
        setRootDirectoryPath(path);
        load();
    }

    Table getUsingTable() {
        return usingTable;
    }

    void setUsingTable(String name) {
        usingTable = getTable(name);
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

    public boolean containsTable(String name) {
        return tableNames.containsKey(name);
    }

    protected void load() {
        File root = getRootDirectory();
        try {
            if (root.exists() && root.isDirectory()) {
                File[] subfolders = getTablesFromRoot(root);
                for (File folder : subfolders) {
                    String name = folder.getName();
                    TableHash table = new TableHash(name, this);
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

    protected File[] getTablesFromRoot(File root) {
        File[] subfolders = root.listFiles();
        for (File folder : subfolders) {
            if (!folder.isDirectory()) {
                return whatToDoWithFiles(folder);
            }
        }
        return subfolders;
    }

    private File[] whatToDoWithFiles(File folder) {
        throw new DatabaseFileStructureException("There is files in root folder. File'"
                + folder.getName() + "");
    }

    public List<Pair<String, Integer>> listTables() {
        int size = tables.size();
        List<Pair<String, Integer>> result = new ArrayList<>(size);
        for (TableHash table : tables) {
            result.add(new Pair<>(table.getTableName(), table.size()));
        }
        return result;
    }

    public void useTable(String name) {
        if (usingTable != null) {
            ((TableHash) usingTable).save();
        }
        setUsingTable(name);
    }

    @Override
    public Table getTable(String name) {
        Table table = tableNames.get(name);
        if (table == null) {
            throw new IllegalArgumentException("Table not exists.");
        } else {
            return table;
        }
    }

    @Override
    public Table createTable(String name) {
        try {
            if (!containsTable(name)) {
                TableHash table = new TableHash(name, this);
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
                return null;
            }
        } catch (UnsupportedOperationException e) {
            throw new LoadOrSaveException(CANT_CREATE_TABLE_MESSAGE, e);
        } catch (FileAlreadyExistsException e) {
            throw new DatabaseFileStructureException(CANT_CREATE_TABLE + ", directory already exists", e);
        } catch (InvalidPathException e) {
            throw new DatabaseFileStructureException(
                    CANT_CREATE_TABLE + ", directory name makes it impossible to create a directory", e);
        } catch (IOException | SecurityException e) {
            throw new LoadOrSaveException(CANT_CREATE_TABLE_MESSAGE, e);
        }
    }

    @Override
    public void removeTable(String name) {
        TableHash table = (TableHash) getTable(name);
        if (table == usingTable) {
            usingTable = null;
        }
        table.drop();
        tables.remove(table);
        tableNames.remove(name);
    }
}

