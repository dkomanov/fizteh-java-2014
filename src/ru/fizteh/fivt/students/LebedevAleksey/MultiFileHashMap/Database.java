package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.*;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParsedCommand;

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
    private List<Table> tables = new ArrayList<Table>();
    private Map<String, Table> tableNames = new TreeMap<>();

    public Database() throws DatabaseFileStructureException, LoadOrSaveError {
        String directoryPath = System.getProperty("fizteh.db.dir");
        setRootDirectoryPath(directoryPath);
        load();
    }

    public Database(String path) throws DatabaseFileStructureException, LoadOrSaveError {
        setRootDirectoryPath(path);
        load();
    }

    protected Table getCurrentTable() {
        return currentTable;
    }

    private void setCurrentTable(String name) throws TableNotFoundException {
        currentTable = getTable(name);
    }

    public File getRootDirectory() throws DatabaseFileStructureException {
        return rootDirectory;
    }

    public Path getRootDirectoryPath() throws DatabaseFileStructureException {
        return getRootDirectory().toPath();
    }

    private void setRootDirectoryPath(String directoryPath) throws DatabaseFileStructureException {
        if (directoryPath == null) {
            throw new DatabaseFileStructureException("Database directory doesn't set");
        } else {
            rootDirectory = new File(directoryPath);
        }
    }

    public void load() throws LoadOrSaveError {
        File root = getRootDirectory();
        try {
            if (root.exists() && root.isDirectory()) {
                File[] subfolders = getTablesFromRoot(root);
                for (File folder : subfolders) {
                    String name = folder.getName();
                    Table table = new Table(name, this);
                    tables.add(table);
                    tableNames.put(name, table);
                }
            } else {
                throw new DatabaseFileStructureException("Root directory not found");
            }
        } catch (SecurityException ex) {
            throw new LoadOrSaveError("Error in loading, access denied: " + ex.getMessage(), ex);
        }
    }

    protected File[] getTablesFromRoot(File root) throws DatabaseFileStructureException {
        File[] subfolders = root.listFiles();
        for (File folder : subfolders) {
            if (!folder.isDirectory()) {
                throw new DatabaseFileStructureException("There is files in root folder. File'" + folder.getName()
                        + "'");
            }
        }
        return subfolders;
    }

    public void list() throws LoadOrSaveError {
        String[] tableNames = new String[tables.size()];
        int[] sizes = new int[tables.size()];
        for (int i = 0; i < tableNames.length; i++) {
            Table table = tables.get(i);
            tableNames[i] = table.getTableName();
            sizes[i] = table.count();
        }
        for (int i = 0; i < tableNames.length; i++) {
            System.out.println(tableNames[i] + " " + sizes[i]);
        }
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
        return tables.contains(name);
    }


    public void useTable(String name) throws TableNotFoundException, LoadOrSaveError {
        if (currentTable != null) {
            currentTable.save();
        }
        setCurrentTable(name);
    }

    Table createTable(String name) throws DatabaseException {
        try {
            if (containsTable(name)) {
                Table table = new Table(name, this);
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
            throw new LoadOrSaveError(CANT_CREATE_TABLE_MESSAGE, ex);
        } catch (FileAlreadyExistsException ex) {
            throw new DatabaseFileStructureException(CANT_CREATE_TABLE + ", directory already exists", ex);
        } catch (InvalidPathException ex) {
            throw new DatabaseFileStructureException(
                    CANT_CREATE_TABLE + ", directory name makes it impossible to create a directory", ex);
        } catch (IOException | SecurityException ex) {
            throw new LoadOrSaveError(CANT_CREATE_TABLE_MESSAGE, ex);
        }
    }

    void removeTable(String name) throws TableNotFoundException, LoadOrSaveError {
        Table table = getTable(name);
        if (table == currentTable) {
            currentTable = null;
        }
        table.drop();
        tables.remove(table);
        tableNames.remove(name);
    }

    protected boolean isTableCommand(String command) {
        switch (command) {
            case "put":
            case "get":
            case "remove":
            case "list":
                return true;
            default:
                return false;
        }
    }


    public void invokeCommand(ParsedCommand command) throws DatabaseException {
        if (isTableCommand(command.getCommandName())) {
            if (currentTable != null) {
                currentTable.invokeCommand(command);
            } else {
                System.out.println("no table");
            }
        } else {
            switch (command.getCommandName()) {
                case "create":
                    tryCreateTable(ArgumentsUtils.get1Args(command));
                    break;
                case "drop":
                    tryRemoveTable(ArgumentsUtils.get1Args(command));
                    break;
                case "use":
                    tryUseTable(ArgumentsUtils.get1Args(command));
                    break;
                case "show":
                    if (ArgumentsUtils.get1Args(command).equals("tables")) {
                        showTables();
                    } else {
                        throw new ArgumentException("Unknown argument in show command");
                    }
                    break;
                default:
                    throw new UnknownCommand();
            }
        }
    }

    private void tryCreateTable(String name) throws DatabaseException {
        try {
            createTable(name);
            System.out.println("created");
        } catch (TableAlreadyExistsException ex) {
            System.out.println(name + " exists");
        }
    }

    private void tryRemoveTable(String name) throws DatabaseException {
        try {
            removeTable(name);
            System.out.println("dropped");
        } catch (TableNotFoundException ex) {
            System.out.println(name + " not exists");
        }
    }

    private void tryUseTable(String name) throws DatabaseException {
        try {
            useTable(name);
            System.out.println("using " + name);
        } catch (TableNotFoundException ex) {
            System.out.println(name + " not exists");
        }
    }

    private void showTables() throws LoadOrSaveError {
        list();
    }
}
