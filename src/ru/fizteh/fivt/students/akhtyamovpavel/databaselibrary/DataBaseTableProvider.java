package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;


import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap.CommitCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap.ListCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap.PutCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap.RollbackCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.table.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by akhtyamovpavel on 07.10.2014.
 */
public class DataBaseTableProvider extends AbstractTableProvider implements AutoCloseable, TableProvider {
    private Path dataBaseDirectory;
    private boolean isInteractive;
    private String openedTableName;
    private DataBaseTable fileMap;
    private HashMap<String, Integer> tableSet;

    public void removeTableFile(String table) {
        tableSet.remove(table);
    }

    public void insertTable(String table) {
        tableSet.put(table, 0);
    }

    public void removeKeyFromTable(String table) {
        int value = tableSet.get(table);
        tableSet.put(table, value - 1);
    }

    public HashMap<String, Integer> getTableSet() {
        return tableSet;
    }

    public void putKeyToTable(String table) {
        int value = tableSet.get(table);
        tableSet.put(table, value + 1);
    }

    public void rollbackTableSize(String table) {
        int value = fileMap.size();
        tableSet.put(table, value);
    }

    public DataBaseTableProvider(String dir) {
        try {
            initDataBaseDirectory(dir);
            onLoadCheck();
        } catch (Exception e) {
            printException(e.getMessage());
        }
        initCommands();
        try {
            initTableSizes();
        } catch (Exception e) {
            printException(e.getMessage());
        }
    }

    private void initDataBaseDirectory(String dir) {
        dataBaseDirectory = Paths.get(System.getProperty("user.dir")).resolve(dir);
        fileMap = null;
        tableSet = new HashMap<String, Integer>();

    }

    private void initCommands() {
        commandNames = new HashMap<String, Command>();

        addCommand(new CreateTableCommand(this));
        addCommand(new DropTableCommand(this));
        addCommand(new ExitCommand(this));
        addCommand(new UseCommand(this, false));
        addCommand(new ShowTablesCommand(this));
    }

    private void onLoadCheck() throws Exception {
        if (!Files.exists(dataBaseDirectory)) {
            throw new Exception("connect: no such database");
        }
        if (!Files.isDirectory(dataBaseDirectory)) {
            throw new Exception("connect: path isn't a directory");
        }
        if (!Files.isReadable(dataBaseDirectory)) {
            throw new Exception("connect: permission denied");
        }
        if (!Files.isWritable(dataBaseDirectory)) {
            throw new Exception("connect: permission denied");
        }

        File currentFolder = dataBaseDirectory.toFile();
        String[] listOfFiles = currentFolder.list();
        TreeSet<String> formats = new TreeSet<>();
        for (int fileIndex = 0; fileIndex < DataBaseTable.NUMBER_OF_DIRECTORIES; ++fileIndex) {
            formats.add(String.format("%02d.dir", fileIndex));
        }
        for (String name : listOfFiles) {
            Path newPath = Paths.get(dataBaseDirectory.toString(), name);
            if (!Files.isDirectory(newPath)) {
                throw new Exception("connect: table " + name + " is extra");
            }
            if (!Files.isReadable(newPath)) {
                throw new Exception("connect: table " + name + ": permission denied");
            }
            if (!Files.isWritable(newPath)) {
                throw new Exception("connect: table " + name + ": permission denied");
            }

            String[] listOfFolders = newPath.toFile().list();

            for (String interFolder: listOfFolders) {
                if (!formats.contains(interFolder)) {
                    throw new Exception("connection: database was broken");
                }
                Path interPath = Paths.get(newPath.toString(), interFolder);
                if (!Files.isReadable(interPath) || !Files.isWritable(interPath)) {
                    throw new Exception("connect: database was broken");
                }
                if (!Files.isDirectory(interPath)) {
                    throw new Exception("connect: database was broken");
                }
            }
        }

    }

    private void initTableSizes() throws Exception {
        String[] listOfTables = dataBaseDirectory.toFile().list();
        for (String table: listOfTables) {
            ArrayList<String> argument = new ArrayList<>();
            argument.add(table);
            new UseCommand(this, true).executeCommand(argument);
            tableSet.put(table, fileMap.size());
        }
        fileMap = null;
        openedTableName = null;
    }

    @Override
    public void close() throws Exception {
        if (fileMap != null) {
            fileMap.saveMap();
        }
    }

    private void addCommand(Command command) {
        commandNames.put(command.getName(), command);
    }



    public Path getDataBaseDirectory() {
        return dataBaseDirectory;
    }

    public void setFileMap(DataBaseTable fileMap) {
        this.fileMap = fileMap;
    }


    public DataBaseTable getOpenedTable() {
        return fileMap;
    }

    public String getOpenedTableName() {
        return openedTableName;
    }

    public void setOpenedTableName(String openedTableName) {
        this.openedTableName = openedTableName;
    }

    public void refreshCommands() {
        commandNames.remove("put");
        commandNames.remove("get");
        commandNames.remove("list");
        commandNames.remove("remove");
        commandNames.remove("commit");
        commandNames.remove("rollback");
        addCommand(new PutCommand(this));
        addCommand(new ListCommand(this));
        addCommand(new PutCommand(this));
        addCommand(new ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap.RemoveCommand(this));
        addCommand(new RollbackCommand(this));
        addCommand(new CommitCommand(this));
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null table name");
        }
        if (name.equals(openedTableName)) {
            return fileMap;
        }
        try {
            fileMap = new DataBaseTable(getDataBaseDirectory(), name);
        } catch (Exception e) {
            return null;
        }
        openedTableName = name;
        return fileMap;
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException, IllegalStateException {

        if (name == null) {
            throw new IllegalArgumentException("null table name");
        }
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(name);
        DataBaseTable table = null;
        try {
            new CreateTableCommand(this).executeCommand(arguments);
            table = new DataBaseTable(dataBaseDirectory, name);
        } catch (FileAlreadyExistsException fae) {
            return null;
        } catch (Exception e) {
            System.err.println("connection error");
        }
        return table;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("null table name");
        }
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(name);

        try {
            new DropTableCommand(this).executeCommand(arguments);
        } catch (FileNotFoundException fae) {
            throw new IllegalStateException("table doesn't exist");
        } catch (Exception e) {
            System.err.println("remove error");
        }
    }
}
