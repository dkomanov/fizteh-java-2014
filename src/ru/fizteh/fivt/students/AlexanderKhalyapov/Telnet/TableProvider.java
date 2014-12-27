package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public final class TableProvider implements ru.fizteh.fivt.storage.structured.TableProvider, AutoCloseable {
    private static final String ERROR_CONNECTING_TO_DATABASE_MSG = "Error connecting to database";
    private static final String DOES_NOT_MATCH_JSON_FORMAT_MSG = "String doesn't match JSON format";
    private static final String TABLE_NAME_IS_NULL_MSG = "Table name is null";
    private static final String ILLEGAL_TABLE_NAME_MSG = "Illegal table name";
    private static final String ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG = "contain filesystem separator or/and '.', '..'";
    private static final Map<Class<?>, Function<String, Object>> PARSER_METHODS;
    static {
        Map<Class<?>, Function<String, Object>> unitializerMap = new HashMap<>();
        unitializerMap.put(Integer.class, Integer::parseInt);
        unitializerMap.put(Long.class, Long::parseLong);
        unitializerMap.put(Byte.class, Byte::parseByte);
        unitializerMap.put(Float.class, Float::parseFloat);
        unitializerMap.put(Double.class, Double::parseDouble);
        unitializerMap.put(Boolean.class, string -> {
            if (!string.matches("(?i)true|false")) {
                throw new ColumnFormatException("Expected 'true' or 'false'");
            }
            return Boolean.parseBoolean(string);
        });
        unitializerMap.put(String.class, string -> {
            if (string.charAt(0) != '"' || string.charAt(string.length() - 1) != '"') {
                throw new ColumnFormatException("String must be quoted");
            }
            return string.substring(1, string.length() - 1);
        });
        PARSER_METHODS = Collections.unmodifiableMap(unitializerMap);
    }
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private AtomicBoolean invalid = new AtomicBoolean(false);
    private Map<String, DbTable> tables = new HashMap<>();
    private Path tablesDirectoryPath;

    public TableProvider(String directoryPath) throws IOException {
        try {
            tablesDirectoryPath = Paths.get(directoryPath);
            if (!tablesDirectoryPath.toFile().exists()) {
                tablesDirectoryPath.toFile().mkdir();
            }
            if (!tablesDirectoryPath.toFile().isDirectory()) {
                throw new IllegalArgumentException(ERROR_CONNECTING_TO_DATABASE_MSG
                        + ": path is incorrect or does not lead to a directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ERROR_CONNECTING_TO_DATABASE_MSG + ": '" + directoryPath
                    + "' is illegal directory name", e);
        }
        for (String tableDirectoryName : tablesDirectoryPath.toFile().list()) {
            Path tableDirectoryPath = tablesDirectoryPath.resolve(tableDirectoryName);
            if (tableDirectoryPath.toFile().isDirectory()) {
                try {
                    tables.put(tableDirectoryName, new DbTable(this, tableDirectoryPath));
                } catch (DataBaseIOException e) {
                    throw new DataBaseIOException(ERROR_CONNECTING_TO_DATABASE_MSG + ": " + e.getMessage(), e);
                }
            } else {
                throw new DataBaseIOException(ERROR_CONNECTING_TO_DATABASE_MSG
                        + ": root directory contains non-directory files");
            }
        }
    }

    @Override
    public Table getTable(String tableName) {
        lock.readLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            if (tableName == null) {
                throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
            }
            tablesDirectoryPath.resolve(tableName);
            if (tableName.matches(Helper.ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG);
            }
            return tables.get(tableName);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + ": " + e.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> columnTypes) throws IOException {
        lock.writeLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            if (tableName == null) {
                throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
            }
            if (tableName.matches(Helper.ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG);
            }
            if (tables.containsKey(tableName)) {
                return null;
            }
            Path newTablePath = tablesDirectoryPath.resolve(tableName);
            newTablePath.toFile().mkdir();
            writeSignature(newTablePath.resolve(Helper.SIGNATURE_FILE_NAME), columnTypes);
            DbTable newTable = new DbTable(this, newTablePath);
            tables.put(tableName, newTable);
            return newTable;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + ": " + e.getMessage(), e);
        } catch (DataBaseIOException e) {
            throw new DataBaseIOException("Unable to create table '" + tableName + "': " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to create table: " + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void writeSignature(Path filePath, List<Class<?>> columnTypes) throws DataBaseIOException {
        checkTableManagerIsNotInvalid();
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("List of column types can't be null or empty");
        }
        try (PrintWriter printer = new PrintWriter(filePath.toString())) {
            StringBuilder typesStringBuilder = new StringBuilder();
            for (Class<?> typeClass : columnTypes) {
                typesStringBuilder.append(Helper.SUPPORTED_TYPES_TO_NAMES.get(typeClass));
                typesStringBuilder.append(" ");
            }
            typesStringBuilder.deleteCharAt(typesStringBuilder.length() - 1);
            printer.print(typesStringBuilder.toString());
        } catch (IOException e) {
            throw new DataBaseIOException("Unable write signature to " + filePath.toString(), e);
        }
    }

    @Override
    public void removeTable(String tableName) throws DataBaseIOException {
        lock.writeLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            if (tableName == null) {
                throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
            }
            if (tableName.matches(Helper.ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG);
            }
            Path tableDirectory = tablesDirectoryPath.resolve(tableName);
            DbTable removedTable = tables.remove(tableName);
            if (removedTable == null) {
                throw new IllegalStateException("There is no such table");
            } else {
                Helper.recoursiveDelete(tableDirectory);
                removedTable.close();
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DataBaseIOException("Unable to remove table: " + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        lock.readLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            List<Class<?>> types = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); i++) {
                types.add(table.getColumnType(i));
            }
            return new Serializer(types);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        lock.readLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            List<Class<?>> types = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); i++) {
                types.add(table.getColumnType(i));
            }
            Storeable store = new Serializer(types);
            for (int i = 0; i < values.size(); i++) {
                store.setColumnAt(i, values.get(i));
            }
            return store;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        lock.readLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            value = value.trim();
            if (value.charAt(0) != '[') {
                throw new ParseException(DOES_NOT_MATCH_JSON_FORMAT_MSG + ": '[' at the begging is missing", 0);
            }
            if (value.charAt(value.length() - 1) != ']') {
                throw new ParseException(DOES_NOT_MATCH_JSON_FORMAT_MSG + ": ']' at the end is missing", 0);
            }
            value = value.substring(1, value.length() - 1);
            String[] tokens = value.split("," + Helper.IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX);
            if (tokens.length != table.getColumnsCount()) {
                throw new ParseException("Incorrect number of tokens: " + table.getColumnsCount() + " expected, but "
                        + tokens.length + " found.", 0);
            }
            Storeable deserialized = createFor(table);
            for (int columnIndex = 0; columnIndex < tokens.length; columnIndex++) {
                try {
                    String token = tokens[columnIndex].trim();
                    if (token.equals("null")) {
                        deserialized.setColumnAt(columnIndex, null);
                    } else {
                        deserialized.setColumnAt(columnIndex, PARSER_METHODS.get(table.getColumnType(columnIndex))
                                .apply(token));
                    }
                } catch (ColumnFormatException e) {
                    throw new ParseException("Token " + columnIndex + " is not correct: " + e.getMessage(), 0);
                } catch (NumberFormatException e) {
                    throw new ParseException("Token " + columnIndex + " is not correct: " + "Input string '"
                            + tokens[columnIndex].trim() + "' is not a number of '"
                            + table.getColumnType(columnIndex).getSimpleName() + "' type", 0);
                }
            }
            return deserialized;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        lock.readLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            String[] tokens = new String[table.getColumnsCount()];
            for (int columnIndex = 0; columnIndex < tokens.length; columnIndex++) {
                try {
                    Object tokenType = table.getColumnType(columnIndex);
                    Object token = Helper.GETTERS.get(tokenType).invoke(value, columnIndex);
                    if (token == null) {
                        tokens[columnIndex] = null;
                    } else if (token.getClass().equals(String.class)) {
                        tokens[columnIndex] = '"' + token.toString() + '"';
                    } else {
                        tokens[columnIndex] = token.toString();
                    }
                } catch (InvocationTargetException e) {
                    throw new ColumnFormatException(e.getTargetException().getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Unable to serialize: " + e.getMessage(), e);
                }
            }
            return '[' + String.join(", ", tokens) + ']';
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> getTableNames() {
        lock.readLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            List<String> namesList = new LinkedList<>();
            namesList.addAll(tables.keySet());
            return namesList;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void checkTableManagerIsNotInvalid() {
        if (invalid.get()) {
            throw new IllegalStateException("This table manager is invalid");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tablesDirectoryPath.toAbsolutePath() + "]";
    }

    void removeTableFromList(String tableName) {
        lock.writeLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            tables.remove(tableName);
        } catch (IllegalStateException e) {
            // Do nothing.
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            checkTableManagerIsNotInvalid();
            invalid.set(true);
            for (Entry<String, DbTable> pair : tables.entrySet()) {
                pair.getValue().close();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
