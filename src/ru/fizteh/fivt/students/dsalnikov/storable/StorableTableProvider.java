package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.dsalnikov.serializer.JSONSerializer;
import ru.fizteh.fivt.students.dsalnikov.utils.CorrectnessCheck;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class StorableTableProvider implements TableProvider, RemoteTableProvider, AutoCloseable {
    private final Lock lock = new ReentrantLock(true);
    private Map<String, StorableTable> tablesMap = new HashMap<>();

    private File tablesDirectory;
    private WorkStatus status;

    public StorableTableProvider(File atDirectory) throws IOException {
        if (atDirectory == null) {
            throw new IllegalArgumentException("Directory is not set");
        }
        if (!atDirectory.exists()) {
            if (!atDirectory.mkdirs()) {
                throw new IOException("storeable table factory create: table provider unavailable");
            }
        } else if (!atDirectory.isDirectory()) {
            throw new IllegalArgumentException(atDirectory.getName() + ": not a directory");
        }
        tablesDirectory = atDirectory;
        status = WorkStatus.WORKING;
        for (File tableFile : tablesDirectory.listFiles()) {
            StorableTable table = new StorableTable(tableFile, this);
            tablesMap.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) {
        status.canBeSafelyUsed();
        if (!CorrectnessCheck.isCorrectName(tableName)) {
            throw new IllegalArgumentException("get table: name is bad");
        }
        lock.lock();
        try {
            StorableTable getRes = tablesMap.get(tableName);
            if (getRes == null) {
                return null;
            }
            if (getRes.isOkForOperations()) {
                return getRes;
            } else {
                File tableFile = new File(tablesDirectory, tableName);
                StorableTable newTable = null;
                try {
                    newTable = new StorableTable(tableFile, this);
                } catch (IOException exc) {
                    throw new IllegalStateException("getTable error:", exc);
                }
                tablesMap.put(tableName, newTable);
                return newTable;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> columnTypes) throws IOException {
        status.canBeSafelyUsed();
        if (!CorrectnessCheck.isCorrectName(tableName) || !CorrectnessCheck.isCorrectColumnTypes(columnTypes)) {
            throw new IllegalArgumentException("create table: name or column types is bad");
        }
        File tableFile = new File(tablesDirectory, tableName);
        lock.lock();
        try {
            if (!tableFile.mkdir()) {
                if (tablesMap.get(tableName).isOkForOperations()) {
                    return null;
                } else {
                    return getTable(tableName);
                }
            }
            StorableTable newTable = new StorableTable(tableFile, columnTypes, this);
            tablesMap.put(tableName, newTable);
            return newTable;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeTable(String tableName) {
        status.canBeSafelyUsed();
        if (!CorrectnessCheck.isCorrectName(tableName)) {
            throw new IllegalArgumentException("remove table: name is bad");
        }
        lock.lock();
        try {
            if (tablesMap.get(tableName) == null) {
                throw new IllegalStateException(tableName + " not exists");
            }
            File tableFile = new File(tablesDirectory, tableName);
            try {
                FileUtils.forceRemoveDirectory((tableFile));
            } catch (IOException exc) {
                throw new IllegalStateException("removeTable error:", exc);
            }
            tablesMap.remove(tableName);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        status.canBeSafelyUsed();
        if (value == null) {
            throw new ParseException("storeable table provider: deserialize: value can not be null", 0);
        }
        return JSONSerializer.deserialize(table, value, this);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        status.canBeSafelyUsed();
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        if (!CorrectnessCheck.correctStoreable(value, columnTypes)) {
            throw new ColumnFormatException("storeable table provider: serialize: bad value");
        }
        return JSONSerializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        status.canBeSafelyUsed();
        return new Storable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        status.canBeSafelyUsed();
        return new Storable(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return tablesMap.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public String toString() {
        status.canBeSafelyUsed();
        return getClass().getSimpleName() + "[" + tablesDirectory + "]";
    }

    @Override
    public void close() {
        status.canBeSafelyUsed();
        for (String tableName : tablesMap.keySet()) {
            tablesMap.get(tableName).close();
        }
        status = WorkStatus.CLOSED;
    }

    public boolean isOkForOperations() {
        try {
            status.canBeSafelyUsed();
        } catch (IllegalStateException exc) {
            return false;
        }
        return true;
    }

    public File getTablesDirectoryFile() {
        return tablesDirectory;
    }

}
