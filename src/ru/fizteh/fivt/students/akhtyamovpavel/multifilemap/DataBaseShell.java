package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap;


import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.filemap.*;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table.CreateTableCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table.DropTableCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table.ShowTablesCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table.UseCommand;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by akhtyamovpavel on 07.10.2014.
 */
public class DataBaseShell extends AbstractShell implements AutoCloseable {
    private Path dataBaseDirectory;
    private boolean isInteractive;
    private String openedTableName;
    private FileMap fileMap;
    private HashMap<String, Integer> tableSet;

    public void removeTable(String table) {
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

    public DataBaseShell() {
        initDataBaseDirectory();
        try {
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

    private void initDataBaseDirectory() {
        dataBaseDirectory = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
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
        for (int fileIndex = 0; fileIndex < FileMap.NUMBER_OF_DIRECTORIES; ++fileIndex) {
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
        fileMap.clear();
        fileMap = null;
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

    public FileMap getFileMap() {
        return fileMap;
    }

    public Path getDataBaseDirectory() {
        return dataBaseDirectory;
    }

    public void setFileMap(FileMap fileMap) {
        this.fileMap = fileMap;
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
        addCommand(new ListCommand(this));
        addCommand(new PutCommand(this));
        addCommand(new RemoveCommand(this));
        addCommand(new GetCommand(this));
    }
}
