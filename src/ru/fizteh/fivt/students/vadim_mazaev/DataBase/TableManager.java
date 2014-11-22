package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import java.util.function.Function;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public final class TableManager implements TableProvider {
    private static final String TABLE_NAME_IS_NULL_MSG = "Table name is null";
    private static final String ILLEGAL_TABLE_NAME_MSG = "Illegal table name: ";
    private static final String ILLEGAL_CHAR_IN_TABLE_NAME_MSG = "contains '\\',  or '/',  or '.'";
    private static final String ILLEGAL_TABLE_NAME_REGEX = ".*\\.|\\..*|.*(/|\\\\).*";
    public static final String SIGNATURE_FILE_NAME = "signature.tsv";
    private static final Map<Class<?>, String> TYPE_NAMES_MAP;
    static {
        Map<Class<?>, String> unitializerMap = new HashMap<>();
        unitializerMap.put(Integer.class, "int");
        unitializerMap.put(Long.class, "long");
        unitializerMap.put(Byte.class, "byte");
        unitializerMap.put(Float.class, "float");
        unitializerMap.put(Double.class, "double");
        unitializerMap.put(Boolean.class, "boolean");
        unitializerMap.put(String.class, "String");
        TYPE_NAMES_MAP = Collections.unmodifiableMap(unitializerMap);
    }
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
    private Map<String, Table> tables;
    private Path tablesDirectoryPath;
    
    /**
     * @param directoryPath Path to the directory containing the tables. 
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
                throw new IllegalArgumentException("Error connecting database"
                        + ": path is incorrect or does not lead to a directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Error connecting database"
                    + ": '" + directoryPath + "' is illegal directory name", e);
        }
        tables = new HashMap<>();
        for (String tableDirectoryName : tablesDirectoryPath.toFile().list()) {
            Path tableDirectoryPath = tablesDirectoryPath.resolve(tableDirectoryName);
            if (tableDirectoryPath.toFile().isDirectory()) {
                try {
                    tables.put(tableDirectoryName, new DbTable(this, tableDirectoryPath));
                } catch (DataBaseIOException e) {
                    throw new DataBaseIOException("Error connecting database: "
                            + e.getMessage(), e);
                }
            } else {
                throw new DataBaseIOException("Error connecting database"
                        + ": root directory contains non-directory files");
            }
        }
    }
    
    /**
     * Returns table with the specified name. Consecutive calls a method with the same
     * arguments should return the same object table if it has not been removed
     * by {@link #removeTable(String)}
     * @param tableName name of the table.
     * @return Object represents table. If table with specified name doesn't exist returns null.
     * @throws IllegalArgumentException If the name of the table is null or invalid.
     */
    @Override
    public Table getTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
        }
        try {
            tablesDirectoryPath.resolve(tableName);
            if (tableName.matches(ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_CHAR_IN_TABLE_NAME_MSG);
            }
            return tables.get(tableName);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + e.getMessage(), e);
        }
    }

    /**
     * Creates table with specified name.
     * Creates new table. Commits the necessary disk operations.
     * @param tableName Name of the table.
     * @param columnTypes Types of table columns. Can't be empty.
     * @return Object represents table. If table with specified name exists returns null.
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
        try {
            if (tableName.matches(ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_CHAR_IN_TABLE_NAME_MSG);
            }
            if (tables.containsKey(tableName)) {
                return null;
            }
            Path newTablePath = tablesDirectoryPath.resolve(tableName);
            newTablePath.toFile().mkdir();
            writeSignature(newTablePath.resolve(SIGNATURE_FILE_NAME), columnTypes);
            Table newTable = new DbTable(this, newTablePath);
            tables.put(tableName, newTable);
            return newTable;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + e.getMessage(), e);
        } catch (DataBaseIOException | IllegalArgumentException e) {
            throw new DataBaseIOException("Unable to create table '" + tableName
                    + "':" + e.getMessage(), e);
        }
    }
    
    /**
     * Write signature to {@value #SIGNATURE_FILE_NAME} file on disk.
     * @param filePath Path to {@value #SIGNATURE_FILE_NAME} file.
     * @param columnTypes List of classes represents types of table columns. Can't be empty.
     * @throws DataBaseIOException If method can't create file or write to it.
     * @throws IllegalArgumentException
     */
    private void writeSignature(Path filePath, List<Class<?>> columnTypes)
            throws DataBaseIOException {
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("List of column types can't be null");
        }
        try (PrintWriter printer = new PrintWriter(filePath.toString())) {
            StringBuilder typesStringBuilder = new StringBuilder();
            for (Class<?> typeClass : columnTypes) {
                typesStringBuilder.append(TYPE_NAMES_MAP.get(typeClass));
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
        if (tableName == null) {
            throw new IllegalArgumentException(TABLE_NAME_IS_NULL_MSG);
        }
        try {
            if (tableName.matches(ILLEGAL_TABLE_NAME_REGEX)) {
                throw new InvalidPathException(tableName, ILLEGAL_CHAR_IN_TABLE_NAME_MSG);
            }
            //TODO removed tables should throw IllegalStateException
            Path tableDirectory = tablesDirectoryPath.resolve(tableName);
            if (tables.remove(tableName) == null) {
                throw new IllegalStateException("There is no such table");
            } else {
                recoursiveDelete(tableDirectory.toFile());
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(ILLEGAL_TABLE_NAME_MSG + e.getMessage(), e);
        } catch (IOException e) {
            throw new DataBaseIOException("Unable to remove table: "
                + e.getMessage(), e);
        }
    }
    
    private void recoursiveDelete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File currentFile : file.listFiles()) {
                recoursiveDelete(currentFile);
            }
        }
        if (!file.delete()) {
          throw new IOException("Unable to delete: " + file);
        }
    }
    
    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            types.add(table.getColumnType(i));
        }
        return new Serializer(types);
    }
    
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
    
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        //TODO check the method + add null
        value = value.trim();
        if (value.charAt(0) != '[') {
            throw new ParseException("String doesn't match JSON format. '[' is missing", 0);
        }
        if (value.charAt(value.length() - 1) != ']') {
            throw new ParseException("String doesn't match JSON format. ']' is missing", 0);
        }
        value = value.substring(1, value.length() - 1);
        String[] tokens = value.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
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
                throw new ParseException("Token [" + columnIndex + "] isn't correct: "
                        + e.getMessage(), 0);
            } catch (NumberFormatException e) {
                throw new ParseException("Token [" + columnIndex + "] isn't correct: "
                        + "Input string '" + tokens[columnIndex] + "' isn't number", 0);
            }
        }
        return deserialized;
    }
    
    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        String[] tokens = new String[table.getColumnsCount()];
        Method[] methods = value.getClass().getDeclaredMethods();
        Map<Class<?>, Method> getters = new HashMap<>();
        for (Method method : methods) {
            getters.put(method.getReturnType(), method);
        }
        for (int columnIndex = 0; columnIndex < tokens.length; columnIndex++) {
            try {
                Object tokenType = table.getColumnType(columnIndex);
                Object token = getters.get(tokenType).invoke(value, columnIndex);
                if (token == null) {
                    tokens[columnIndex] = null;
                } else if (token.getClass().equals(String.class)) {
                    tokens[columnIndex] = '"' + token.toString() + '"';
                } else {
                    tokens[columnIndex] = token.toString();
                }
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | IndexOutOfBoundsException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return '[' + String.join(", ", tokens) + ']';
    }
    
    @Override
    public List<String> getTableNames() {
        List<String> namesList = new LinkedList<>();
        namesList.addAll(tables.keySet());
        return namesList;
    }
}
