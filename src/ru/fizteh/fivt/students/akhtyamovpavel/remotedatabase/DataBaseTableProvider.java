package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.fileshell.MakeDirectoryCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.fileshell.RemoveCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by akhtyamovpavel on 07.10.2014.
 */

public class DataBaseTableProvider implements AutoCloseable, TableProvider {
    Path dataBaseDirectory;
    String openedTableName;
    DataBaseTable fileMap;
    HashMap<String, Integer> tableSet;
    TableRowSerializer serializer = new TableRowSerializer();
    HashMap<String, DataBaseTable> tables = new HashMap<>();

    boolean closed;

    /*
        Multi-threading locks
     */
    ReentrantReadWriteLock providerLock = new ReentrantReadWriteLock(true);
    HashMap<String, ReentrantReadWriteLock> tableLocks = new HashMap<>();

    void isClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("database is closed");
        }
    }

    public DataBaseTableProvider(String dir) throws Exception {
        initDataBaseDirectory(dir);
        onLoadCheck();
        initTableData();
    }

    public DataBaseTableProvider(String dir, boolean testMode) throws Exception {
        if (testMode) {
            initDataBaseDirectory(dir);
            onLoadCheck();
            initTableData();
        }
    }

    public void removeTableFile(String table) {
        tableSet.remove(table);
    }

    public void insertTable(String table) {
        tableSet.put(table, 0);
    }


    public HashMap<String, Integer> getTableSet() {
        return tableSet;
    }


    private void initDataBaseDirectory(String dir) {
        dataBaseDirectory = Paths.get(System.getProperty("user.dir")).resolve(dir);
        fileMap = null;
        tableSet = new HashMap<String, Integer>();

    }


    private void onLoadCheck() throws Exception {
        if (!Files.exists(dataBaseDirectory)) {
            try {
                Files.createDirectory(dataBaseDirectory);
            } catch (SecurityException se) {
                throw new Exception("connect: permission to database file denied correctly");
            } catch (IOException ioe) {
                throw new Exception("connect: input/output error");
            }
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

            for (String interFolder : listOfFolders) {
                if (interFolder.equals("signature.tsv")) {
                    continue;
                }
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

    private void initTableData() throws Exception {
        String[] listOfTables = dataBaseDirectory.toFile().list();
        for (String table : listOfTables) {
            ReentrantReadWriteLock currentLock = new ReentrantReadWriteLock(true);
            tables.put(table, new DataBaseTable(dataBaseDirectory, table, this.serializer,
                    currentLock));
            tableLocks.put(table, currentLock);
            tableSet.put(table, getTable(table).size());
        }
        fileMap = null;
        openedTableName = null;
    }

    @Override
    public void close() throws Exception {
        for (Map.Entry<String, DataBaseTable> entry: tables.entrySet()) {
            entry.getValue().close();
        }
    }


    public Path getDataBaseDirectory() {
        isClosed();
        return dataBaseDirectory;
    }

    public void setFileMap(DataBaseTable fileMap) {
        isClosed();
        this.fileMap = fileMap;
    }


    public DataBaseTable getOpenedTable() {
        isClosed();
        return fileMap;
    }

    public String getOpenedTableName() {
        isClosed();
        return openedTableName;
    }

    public void setOpenedTableName(String openedTableName) {
        isClosed();
        this.openedTableName = openedTableName;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        isClosed();
        boolean isClosedCurrentMap = false;
        if (name == null) {
            throw new IllegalArgumentException("null table name");
        }
        providerLock.readLock().lock();
        try {
            if (fileMap != null) {
                fileMap.isClosed();
            }
            if (name.equals(openedTableName)) {
                return fileMap;
            }
            if (onExistCheck(name, true) != null) {
                return null;
            }
        } catch (IllegalStateException e) {
            isClosedCurrentMap = true;
        } finally {
            providerLock.readLock().unlock();
        }

        providerLock.readLock().lock();
        try {
            try {
                if (fileMap != null) {
                    try {
                        fileMap.isClosed();
                    } catch (IllegalStateException e) {
                        isClosedCurrentMap = true;
                    }
                }
                if (!isClosedCurrentMap) {
                    if (fileMap != null && fileMap.hasUnsavedChanges()) {
                        throw new IllegalArgumentException(fileMap.getNumberOfUncommittedChanges()
                                + " unsaved changes");
                    }
                    fileMap = tables.get(name);
                    try {
                        fileMap.isClosed();
                    } catch (IllegalStateException e) {
                        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
                    }
                }
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException(iae.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException("connection error");
            }
            if (!isClosedCurrentMap) {
                openedTableName = name;
                return fileMap;
            }
        } finally {
            providerLock.readLock().unlock();
        }

        providerLock.writeLock().lock();
        try {
            fileMap = null;
            ReentrantReadWriteLock currentLock = new ReentrantReadWriteLock(true);
            fileMap = new DataBaseTable(dataBaseDirectory, name, serializer, currentLock);
            tableLocks.put(name, currentLock);
            tables.put(name, fileMap);
            return fileMap;
        } catch (Exception e) {
            throw new IllegalStateException("new table couldn't opened");
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        isClosed();
        if (name == null || columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("null table name");
        }
        providerLock.writeLock().lock();
        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(name);
            DataBaseTable table = null;
            if (onExistCheck(name, false) != null) {
                return null;
            }
            try {
                new MakeDirectoryCommand(this).executeCommand(arguments);
                ReentrantReadWriteLock currentLock = new ReentrantReadWriteLock(true);
                table = new DataBaseTable(dataBaseDirectory, name, columnTypes, serializer, currentLock);
                tableLocks.put(name, currentLock);
                insertTable(name);
                tables.put(name, table);
            } catch (Exception e) {
                throw new IllegalArgumentException("incorrect name of table");
            }
            return table;
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    public String onExistCheck(String name, boolean existMode) {
        isClosed();
        Path newPath = Paths.get(getDataBaseDirectory().toString(), name);
        if (!Files.exists(newPath) && existMode) {
            return name + " not exists";
        }
        if (Files.exists(newPath) && !existMode) {
            return name + " exists";
        }
        return null;
    }


    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        isClosed();
        if (name == null) {
            throw new IllegalArgumentException("null table name");
        }
        if (!tableSet.containsKey(name)) {
            throw new IllegalStateException(name + " not exists");
        }

        providerLock.writeLock().lock();
        try {
            ArrayList<String> argumentsForRemove = new ArrayList<String>();
            argumentsForRemove.add("-r");
            argumentsForRemove.add(name);
            try {
                new RemoveCommand(getDataBaseDirectory()).executeCommand(argumentsForRemove);
            } catch (Exception e) {
                throw new IllegalArgumentException("wrong state");
            }

            removeTableFile(name);
            tables.remove(name);
            if (name.equals(getOpenedTableName())) {
                setOpenedTableName(null);
                setFileMap(null);
            }
        } finally {
            providerLock.writeLock().unlock();
        }

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        isClosed();
        return serializer.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        isClosed();
        return serializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        isClosed();
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        return new TableRow(values);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        isClosed();
        List<Object> objectValues = new ArrayList<>(values);

        for (int i = 0; i < values.size(); ++i) {
            if (objectValues.get(i) == null) {
                continue;
            }
            if (objectValues.get(i).getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException("incompatible column types");
            }
        }
        return new TableRow(objectValues);
    }

    @Override
    public List<String> getTableNames() {
        isClosed();
        List<String> result = new ArrayList<>();
        for (String currentString: tables.keySet()) {
            result.add(currentString);
        }
        return result;
        //There is an extra implementation method for my realization, watch below
    }

    public HashMap<String, Integer> getTableList() {
        isClosed();
        providerLock.readLock().lock();
        try {
            HashMap<String, Integer> tableList = new HashMap<>();
            for (Map.Entry<String, DataBaseTable> entry : tables.entrySet()) {
                tableList.put(entry.getKey(), entry.getValue().size());
            }
            return tableList;
        } finally {
            providerLock.readLock().unlock();
        }
    }

}
