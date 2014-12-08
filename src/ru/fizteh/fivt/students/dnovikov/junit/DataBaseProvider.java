package ru.fizteh.fivt.students.dnovikov.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.TableNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

public class DataBaseProvider implements TableProvider {
    private Path rootDirectory;

    private ArrayList<Table> tables = new ArrayList<>();
    private Map<String, Table> tableNames = new TreeMap<>();

    public DataBaseProvider(String directoryPath) {
        if (directoryPath == null) {
            throw new IllegalArgumentException("database directory not set");
        } else {
            rootDirectory = new File(directoryPath).toPath();
        }
        loadTables();
    }

    public Path getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("cannot get table: name should be non-null string");
        }
        return tableNames.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("cannot create table");
        }
        if (tableNames.get(name) != null) {
            return null;
        } else {
            try {
                Path newDir = rootDirectory.resolve(name);
                Files.createDirectory(newDir);
            } catch (IOException e) {
                throw new LoadOrSaveException("can't create directory: " + rootDirectory + File.separator + name);
            } catch (InvalidPathException e) {
                throw new LoadOrSaveException("invalid path");
            }
            DataBaseTable table = new DataBaseTable(name, this);
            tableNames.put(name, table);
            tables.add(table);
            return table;
        }
    }

    @Override
    public void removeTable(String name) throws TableNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("cannot remove table: null");
        }
        DataBaseTable table = (DataBaseTable) tableNames.get(name);
        if (table == null) {
            throw new TableNotFoundException();
        } else {
            tableNames.remove(name);
            table.drop();
            tables.remove(table);
        }
    }

    public List<TableInfo> showTable() {
        List<TableInfo> result = new ArrayList<>();
        for (Table table : tables) {
            String tableName = table.getName();
            int size = table.size();
            result.add(new TableInfo(tableName, size));
        }
        return result;
    }

    public void loadTables() {
        if (rootDirectory.toFile().isDirectory()) {
            File[] foldersInRoot = rootDirectory.toFile().listFiles();
            if (foldersInRoot == null) {
                throw new LoadOrSaveException("can't load database");
            }
            for (File folder : foldersInRoot) {
                if (!folder.isDirectory()) {
                    throw new LoadOrSaveException("file '" + folder.getName() + "' in root directory");
                }
            }
            for (File folder : foldersInRoot) {
                DataBaseTable currentTable = new DataBaseTable(folder.getName(), this);
                tables.add(currentTable);
                tableNames.put(folder.getName(), currentTable);
            }
        } else if (!rootDirectory.toFile().exists()) {
            throw new LoadOrSaveException("root directory '" + rootDirectory.getFileName() + "' not found");
        } else {
            throw new LoadOrSaveException("root directory '" + rootDirectory.getFileName() + "' is not directory");
        }
    }
}
