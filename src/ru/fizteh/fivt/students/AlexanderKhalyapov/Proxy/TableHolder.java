package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableHolder implements TableProvider, AutoCloseable {
    private Path rootPath;
    private Map<String, DBTable> tableMap;
    private ReadWriteLock tableAccessLock = new ReentrantReadWriteLock(true);

    private boolean valid = true;
    private ReadWriteLock validLock = new ReentrantReadWriteLock(true);

    public TableHolder(final String rootDir) throws IOException {
        try {
            rootPath = Paths.get(rootDir);
            if (!Files.exists(rootPath)) {
                Files.createDirectory(rootPath);
            }
            if (!Files.isDirectory(rootPath)) {
                throw new IllegalArgumentException(rootDir
                        + " is not directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(rootDir
                    + "' is illegal directory name", e);
        }
        tableMap = new HashMap<>();
        Utility.checkDirectorySubdirectories(rootPath);
        try (DirectoryStream<Path> databaseStream = Files.newDirectoryStream(rootPath)) {
            for (Path tableDirectory : databaseStream) {
                String tableName = tableDirectory.getFileName().toString();
                tableMap.put(tableName, new DBTable(rootPath, tableName, this));
            }
        }
    }

    @Override
    public Table getTable(String name) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkTableName(name);
            tableAccessLock.readLock().lock();
            try {
                if (tableMap.containsKey(name)) {
                    try {
                        DBTable table = tableMap.get(name);
                        table.checkIfValid();
                        return table;
                    } catch (IllegalStateException e) {
                        try {
                            tableMap.put(name, new DBTable(rootPath, name, this));
                        } catch (IOException io) {
                            throw new DatabaseFormatException("Can't load table", io);
                        }
                    }
                    return tableMap.get(name);
                } else {
                    return null;
                }
            } finally {
                tableAccessLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }


    private Table createNewInstanceTable(String name, List<Class<?>> columnTypes) throws IOException {
        Path pathTableDirectory = rootPath.resolve(name);
        Files.createDirectory(pathTableDirectory);
        DBTable newTable = new DBTable(rootPath,
                name, new HashMap<>(), columnTypes, this);
        Path tableSignaturePath = pathTableDirectory.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            for (Class type : columnTypes) {
                String s = Utility.WRAPPERS_TO_PRIMITIVE.get(type) + " ";
                writeSig.write(s.getBytes(Utility.ENCODING));
            }
        }
        tableMap.put(name, newTable);
        return newTable;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkTableName(name);
            Utility.checkTableColumnTypes(columnTypes);
            tableAccessLock.writeLock().lock();
            try {
                DBTable table = tableMap.get(name);
                if (table == null) {
                    return createNewInstanceTable(name, columnTypes);
                } else {
                    try {
                        table.checkIfValid();
                        return null;
                    } catch (IllegalStateException e) {
                        Utility.recursiveDeleteCopy(rootPath.resolve(name));
                        return createNewInstanceTable(name, columnTypes);
                    }
                }
            } finally {
                tableAccessLock.writeLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkTableName(name);
            tableAccessLock.writeLock().lock();
            try {
                DBTable table = tableMap.get(name);
                if (table == null) {
                    throw new IllegalStateException(name + " doesn't exist");
                }
                Path tableDirectory = rootPath.resolve(table.getName());
                table.close();
                Utility.recursiveDeleteCopy(tableDirectory);
                tableMap.remove(name);
            } finally {
                tableAccessLock.writeLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            List<String> values = Utility.parseString(value);
            List<Object> tableValues = Utility.formatStringValues(table, values);
            try {
                return createFor(table, tableValues);
            } catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public String serialize(Table table, Storeable value) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            List<String> storeableValues = Utility.getStoreableValues(table, value);
            String joined = String.join(Utility.FORMATTER, storeableValues);
            return String.valueOf(Utility.VALUE_START_LIMITER) + joined + Utility.VALUE_END_LIMITER;
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            int size = table.getColumnsCount();
            List<Class<?>> requiredTypes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                requiredTypes.add(table.getColumnType(i));
            }
            return new Record(requiredTypes);
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Storeable aimedRecord = createFor(table);
            int size = table.getColumnsCount();
            if (values.size() != size) {
                throw new IndexOutOfBoundsException("wrong amount of columns");
            } else {
                for (int i = 0; i < size; i++) {
                    Object currentValue = values.get(i);
                    aimedRecord.setColumnAt(i, currentValue);
                }
                return aimedRecord;
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public List<String> getTableNames() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            List<String> tableNames = new ArrayList<>();
            tableMap.forEach((String s, DBTable table) -> tableNames.add(s));
            return tableNames;
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + rootPath.toAbsolutePath().toString() + "]";
    }

    @Override
    public final void close() {
        validLock.writeLock().lock();
        try {
            checkIfValid();
            tableMap.forEach((s, table) -> {
                try {
                    table.close();
                } catch (IllegalStateException e) {
                }
            });
            valid = false;
            tableMap.clear();
        } finally {
            validLock.writeLock().unlock();
        }
    }

    private void checkIfValid() {
        if (!valid) {
            throw new IllegalStateException("TableProvider was closed\n");
        }
    }
}
