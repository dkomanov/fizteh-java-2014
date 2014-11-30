package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Management class for working with the {link Table tables}.
 * 
 * Suggests that the current version from disk is saved when object instance is creating.
 * Further input and output is performed only at the time of table creation and deletion.
 *
 * This class is thread safe.
 */
public final class TableManager implements TableProvider {
    private static final String ERROR_CONNECTING_TO_DATABASE_MSG = "Error connecting to database";
    private static final String DOES_NOT_MATCH_JSON_FORMAT_MSG = "String doesn't match JSON format";
    private static final String TABLE_NAME_IS_NULL_MSG = "Table name is null";
    private static final String ILLEGAL_TABLE_NAME_MSG = "Illegal table name";
    private static final String ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG
        = "contain filesystem separator or/and '.', '..'";
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
    private ReadWriteLock lock;
    private Map<String, Table> tables;
    private Path tablesDirectoryPath;
    
    /**
     * @param directoryPath Path to the directory containing the tables.
     * 
     * @throws DataBaseIOException If structure of data base is corrupted.
     * @throws IllegalArgumentException If path is incorrect or doesn't lead
     * to a directory.
     */
    public TableManager(String directoryPath) throws DataBaseIOException {
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
            throw new IllegalArgumentException(ERROR_CONNECTING_TO_DATABASE_MSG
                    + ": '" + directoryPath + "' is illegal directory name", e);
        }
        tables = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        for (String tableDirectoryName : tablesDirectoryPath.toFile().list()) {
            Path tableDirectoryPath = tablesDirectoryPath.resolve(tableDirectoryName);
            if (tableDirectoryPath.toFile().isDirectory()) {
                try {
                    tables.put(tableDirectoryName, new DbTable(this, tableDirectoryPath));
                } catch (DataBaseIOException e) {
                    throw new DataBaseIOException(ERROR_CONNECTING_TO_DATABASE_MSG
                            + ": " + e.getMessage(), e);
                }
            } else {
                throw new DataBaseIOException(ERROR_CONNECTING_TO_DATABASE_MSG
                        + ": root directory contains non-directory files");
            }
        }
    }
    
    /**
     * Returns table with the specified name. Consecutive calls a method with the same
     * arguments should return the same object table if it has not been removed
     * by {@link #removeTable(String)}.
     * 
     * @param tableName name of the table.
     * @return Object represents table. If table with specified name doesn't exist returns null.
     * 
     * @throws IllegalArgumentException If the name of the table is null or invalid.
     */
    @Override
    public Table getTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
        }
        lock.readLock().lock();
        try {
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

    /**
     * Creates table with specified name.
     * Creates new table. Commits the necessary disk operations.
     * 
     * @param tableName Name of the table.
     * @param columnTypes Types of table columns. Can't be empty.
     * @return Object represents table. If table with specified name exists returns null.
     * 
     * @throws IllegalArgumentException If the name of the table is null or invalid.
     * If columns types list is null or contains invalid values.
     * @throws DataBaseIOException If I/O errors occurred.
     */
    @Override
    public Table createTable(String tableName, List<Class<?>> columnTypes)
            throws DataBaseIOException {
        if (tableName == null) {
            throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
        }
        lock.writeLock().lock();
        try {
            if (tableName.matches(Helper.ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG);
            }
            if (tables.containsKey(tableName)) {
                return null;
            }
            Path newTablePath = tablesDirectoryPath.resolve(tableName);
            newTablePath.toFile().mkdir();
            writeSignature(newTablePath.resolve(Helper.SIGNATURE_FILE_NAME), columnTypes);
            Table newTable = new DbTable(this, newTablePath);
            tables.put(tableName, newTable);
            return newTable;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + ": " + e.getMessage(), e);
        } catch (DataBaseIOException | IllegalArgumentException e) {
            throw new DataBaseIOException("Unable to create table '" + tableName
                    + "': " + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Write signature to {@value #SIGNATURE_FILE_NAME} file on disk.
     * 
     * @param filePath Path to {@value #SIGNATURE_FILE_NAME} file.
     * @param columnTypes List of classes represents types of table columns. Can't be empty.
     * 
     * @throws DataBaseIOException If method can't create file or write to it.
     * @throws IllegalArgumentException If list of column types is null.
     */
    private void writeSignature(Path filePath, List<Class<?>> columnTypes)
            throws DataBaseIOException {
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

    /**
     * Deletes an existing table with the specified name.
     * 
     * Removed table object, if someone took it by a {@link #getTable (String)},
     * from this moment must throw {@link IllegalStateException}.
     * 
     * @param tableName Name of the table.
     * 
     * @throws IllegalArgumentException If table name is null or incorrect.
     * @throws IllegalStateException If table with specified name doesn't exist.
     * @throws DataBaseIOException If I/O errors occurred.
     */
    @Override
    public void removeTable(String tableName) throws DataBaseIOException {
        if (tableName == null) {
            throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
        }
        lock.writeLock().lock();
        try {
            if (tableName.matches(Helper.ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_SYMBOL_IN_TABLE_NAME_MSG);
            }
            Path tableDirectory = tablesDirectoryPath.resolve(tableName);
            DbTable removedTable = (DbTable) tables.remove(tableName);
            if (removedTable == null) {
                throw new IllegalStateException("There is no such table");
            } else {
                Helper.recoursiveDelete(tableDirectory.toFile());
                removedTable.invalidate();
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DataBaseIOException("Unable to remove table: "
                + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Creates new empty {@link Storeable} for specified table.
     * 
     * @param table Table, which {@link Storeable} should belong to.
     * @return Empty {@link Storeable} aimed at the usage with this table.
     */
    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            types.add(table.getColumnType(i));
        }
        return new Serializer(types);
    }
    
    /**
     * Creates new empty {@link Storeable} for specified table, substituting there the values.
     * 
     * @param table Table, which {@link Storeable} should belong to.
     * @param values List of values which are needed for initializing the fields of Storeable.
     * @return {@link Storeable}, initialized with the specified values.
     * 
     * @throws ColumnFormatException If types of provided value and columns
     * don't match each other.
     * @throws IndexOutOfBoundsException If number of columns and provided values
     * don't match each other.
     */
    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException,
        IndexOutOfBoundsException {
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            types.add(table.getColumnType(i));
        }
        Storeable store = new Serializer(types);
        for (int i = 0; i < values.size(); i++) {
            store.setColumnAt(i, values.get(i));
        }
        return store;
    }
    
    /**
     * Converts string in JSON format to {@link Storeable},
     * corresponding to the structure of the table.
     * 
     * @param table Table, which {@link Storeable} should belong to.
     * @param value String, which {@link Storeable} should be read from.
     * @return Read {@link Storeable}.
     * 
     * @throws ParseException If any discrepancies in the read data.
     */
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        value = value.trim();
        if (value.charAt(0) != '[') {
            throw new ParseException(DOES_NOT_MATCH_JSON_FORMAT_MSG
                    + ": '[' at the begging is missing", 0);
        }
        if (value.charAt(value.length() - 1) != ']') {
            throw new ParseException(DOES_NOT_MATCH_JSON_FORMAT_MSG
                    + ": ']' at the end is missing", 0);
        }
        value = value.substring(1, value.length() - 1);
        String[] tokens = value.split("," + Helper.IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX);
        if (tokens.length != table.getColumnsCount()) {
            throw new ParseException("Incorrect number of tokens: "
                    + table.getColumnsCount() + " expected, but " + tokens.length + " found.", 0);
        }
        Storeable deserialized = createFor(table);
        for (int columnIndex = 0; columnIndex < tokens.length; columnIndex++) {
            try {
                String token = tokens[columnIndex].trim();
                if (token.equals("null")) {
                    deserialized.setColumnAt(columnIndex, null);
                } else {
                    deserialized.setColumnAt(columnIndex,
                            PARSER_METHODS.get(table.getColumnType(columnIndex)).apply(token));
                }
            } catch (ColumnFormatException e) {
                throw new ParseException("Token " + columnIndex + " is not correct: "
                        + e.getMessage(), 0);
            } catch (NumberFormatException e) {
                throw new ParseException("Token " + columnIndex + " is not correct: "
                        + "Input string '" + tokens[columnIndex].trim() + "' is not a number of '"
                        + table.getColumnType(columnIndex).getSimpleName() + "' type", 0);
            }
        }
        return deserialized;
    }
    
    /**
     * Converts {@link Storeable} to string in JSON format.
     * 
     * @param table Table, which {@link Storeable} should belong to.
     * @param value {@link Storeable}, which should be writed.
     * @return String in JSON format.
     * 
     * @throws ColumnFormatException If types of provided value and columns of the table
     * don't match each other.
     */
    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
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
    }
    
    /**
     * Returns the names of existing tables.
     * 
     * @return Names of the tables.
     */
    @Override
    public List<String> getTableNames() {
        List<String> namesList = new LinkedList<>();
        namesList.addAll(tables.keySet());
        return namesList;
    }
}
