package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.CastMaker;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.DirectoryKiller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableManager implements TableProvider, AutoCloseable {
    private Map<String, Table> tableManagerMap;
    private final Path databasePath;
    public static final String CODE_FORMAT = "UTF-8";
    private static final String ILLEGAL_FORMAT_MESSAGE = "Incorrect storeable format. Signature of current table: ";
    static final int NUMBER_OF_PARTITIONS = 16;
    static final String FOLDER_NAME_PATTERN = "([0-9]|1[0-5])\\.dir";
    static final String FILE_NAME_PATTERN = "([0-9]|1[0-5])\\.dat";
    private static final String ILLEGAL_TABLE_NAME_PATTERN = ".*\\.|\\..*|.*(/|\\\\).*";
    private static final String REGEXP_TO_SPLIT_JSON = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private ReadWriteLock lock;
    private AtomicBoolean closed = new AtomicBoolean(false);

    public TableManager(String path) throws IllegalArgumentException {
        databasePath = Paths.get(path);
        tableManagerMap = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        if (!databasePath.toFile().exists()) {
            databasePath.toFile().mkdir();
        } else if (!databasePath.toFile().isDirectory()) {
            throw new IllegalArgumentException(path + ": is not a directory");
        }
        String[] tablesNames = databasePath.toFile().list();
        for (String currentTableName : tablesNames) {
            Path currentTablePath = databasePath.resolve(currentTableName);
            if (currentTablePath.toFile().isDirectory()) {
                Table currentTable;
                try {
                    currentTable = new TableClass(currentTablePath, currentTableName, this, null);
                } catch (DatabaseCorruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                tableManagerMap.put(currentTableName, currentTable);
            } else {
                throw new IllegalArgumentException("Database corrupted: unexpected files in root directory");
            }
        }
    }

    @Override
    public Table getTable(String name) {
        checkIfClosed();
        lock.readLock().lock();
        try {
            if (name == null) {
                throw new IllegalArgumentException("getTable: null argument");
            }
            if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
                throw new IllegalArgumentException("getTable: wrong name of table");
            }
            return tableManagerMap.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void replaceClosedTableWithNew(String name, Table wasClosed) throws DatabaseCorruptedException {
        checkIfClosed();
        tableManagerMap.put(name, ((TableClass) wasClosed).makeNewTableFromClosed());
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkIfClosed();
        if (name == null) {
            throw new IllegalArgumentException("createTable: null argument");
        }
        lock.writeLock().lock();
        try {
            if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
                throw new IllegalArgumentException("createTable: wrong name of table");
            }
            Path newTablePath = databasePath.resolve(name);
            if (tableManagerMap.get(name) != null) {
                return null;
            }
            newTablePath.toFile().mkdir();
            try {
                Table newTable = new TableClass(newTablePath, name, this, columnTypes);
                tableManagerMap.put(name, newTable);
                return newTable;
            } catch (DatabaseCorruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) {
        checkIfClosed();
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        lock.writeLock().lock();
        try {
            if (name.matches(ILLEGAL_TABLE_NAME_PATTERN)) {
                throw new IllegalArgumentException("removeTable: wrong name of table");
            }
            Path tableDir = databasePath.resolve(name);
            Table removedTable = tableManagerMap.remove(name);
            if (removedTable == null) {
                throw new IllegalStateException("Table not found");
            } else {
                ((TableClass) removedTable).setRemovedFlag();
                DirectoryKiller.delete(tableDir.toFile());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        checkIfClosed();
        if (!value.startsWith("[")) {
            throw new ParseException("Can't deserialize <" + value + ">: argument doesn't start with \"[\"", 0);
        }
        if (!value.endsWith("]")) {
            throw new ParseException("Can't deserialize <" + value + ">: argument doesn't end with \"]\"", 0);
        }
        value = value.substring(1, value.length() - 1);
        String[] parsedValues = value.split(REGEXP_TO_SPLIT_JSON);
        Storeable answer = createFor(table);
        int currentIndex;
        for (currentIndex = 0; currentIndex < parsedValues.length; ++currentIndex) {
            answer.setColumnAt(currentIndex,
                    CastMaker.excludeValue(parsedValues[currentIndex],
                            table.getColumnType(currentIndex), table, ILLEGAL_FORMAT_MESSAGE));
        }
        if (currentIndex < table.getColumnsCount()) {
            throw new ParseException(ILLEGAL_FORMAT_MESSAGE + CastMaker.getSignatureFormat(table), 0);
        }
        return answer;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkIfClosed();
        StringBuilder answer = new StringBuilder("[");
        for (int currentIndex = 0; currentIndex < table.getColumnsCount(); ++currentIndex) {
            answer.append(excludeFromStoreable(table, value, currentIndex)).append(", ");
        }
        answer.deleteCharAt(answer.length() - 1);
        answer.deleteCharAt(answer.length() - 1);
        answer.append("]");
        return answer.toString();
    }

    private String excludeFromStoreable(Table table, Storeable storeable, int index) {
        checkIfClosed();
        Object answer = null;
        Class<?> currentClass = table.getColumnType(index);
        if (currentClass == Integer.class) {
            answer = storeable.getIntAt(index);
        }
        if (currentClass == Long.class) {
            answer = storeable.getLongAt(index);
        }
        if (currentClass == Byte.class) {
            answer = storeable.getByteAt(index);
        }
        if (currentClass == Float.class) {
            answer = storeable.getFloatAt(index);
        }
        if (currentClass == Double.class) {
            answer = storeable.getDoubleAt(index);
        }
        if (currentClass == Boolean.class) {
            answer = storeable.getBooleanAt(index);
        }
        if (currentClass == String.class) {
            answer = storeable.getStringAt(index);
        }
        if (answer != null) {
            return answer.toString();
        } else {
            return "null";
        }
    }

    @Override
    public Storeable createFor(Table table) {
        checkIfClosed();
        return new StoreableClass(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIfClosed();
        return new StoreableClass(table, values);
    }

    @Override
    public List<String> getTableNames() {
        checkIfClosed();
        List<String> answer = new ArrayList<>();
        for (Table table : tableManagerMap.values()) {
            answer.add(table.getName());
        }
        return answer;
    }

    static int excludeFolderNumber(String folderName) {
        return Integer.parseInt(folderName.substring(0, folderName.length() - 4));
    }
    static int excludeDataFileNumber(String fileName) {
        return Integer.parseInt(fileName.substring(0, fileName.length() - 4));
    }

    @Override
    public void close() throws Exception {
        if (!closed.getAndSet(true)) {
            for (Table currentTable : tableManagerMap.values()) {
                ((TableClass) currentTable).close();
            }
        } else {
            throw new IllegalStateException("TableProvider has already been closed");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + databasePath.toAbsolutePath().toString() + "]";
    }

    private void checkIfClosed() {
        if (closed.get()) {
            throw new IllegalStateException("TableProvider has already been closed");
        }
    }
}
