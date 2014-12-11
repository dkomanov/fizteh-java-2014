package ru.fizteh.fivt.students.PotapovaSofia.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;

public class DbTableProvider implements TableProvider {

    private Map<String, Table> tables;
    private Path dbPath;

    public DbTableProvider(String dir) {
        try {
            dbPath = Paths.get(dir);
            if (!dbPath.toFile().exists()) {
                dbPath.toFile().mkdir();
            }
            if (!dbPath.toFile().isDirectory()) {
                throw new IllegalArgumentException("Error connecting database"
                        + ": path is incorrect or does not lead to a directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Error connecting database: '"
                    + dir + "' is illegal directory name", e);
        }
        tables = new TreeMap<>();

        String[] tablesDirlist = dbPath.toFile().list();
        for (String curTableDir : tablesDirlist) {
            Path curTableDirPath = dbPath.resolve(curTableDir);
            if (curTableDirPath.toFile().isDirectory()) {
                Table curTable = new DbTable(curTableDirPath, curTableDir, this);
                tables.put(curTableDir, curTable);
            } else {
                throw new IllegalArgumentException("Error connecting database"
                        + ": root directory contains non-directory files");
            }
        }
    }

    @Override
    public Table getTable(String name) {
        checkTableName(name);
        String tableName = name;
        if (tables.containsKey(tableName)) {
            if (tables.get(tableName) == null) {
                throw new IllegalStateException("Table was removed");
            }
            return tables.get(tableName);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkTableName(name);
        //check types on available
        if (columnTypes == null) {
            throw new IllegalArgumentException("Wrong type (column type is null)");
        }
        /*
        for (Class<?> columnType : columnTypes) {
            if (!StoreableMain.classesNames.containsKey(columnType)) {
                throw new IllegalArgumentException("Wrong type (" + columnType.toString() + ")");
            }
        }
        */
        if (tables.get(name) != null) {
            return null;
        }
        //creating directory
        Path newTablePath = dbPath.resolve(name);
        newTablePath.toFile().mkdir();
        Table newTable = new DbTable(newTablePath, name, new HashMap<>(), columnTypes, this);
        Path signaturePath = newTablePath.resolve(StoreableMain.TABLE_SIGNATURE);
        signaturePath.toFile().createNewFile();
        try (RandomAccessFile write = new RandomAccessFile(signaturePath.toString(), "rw")) {
            for (Class type : columnTypes) {
                String s = StoreableMain.avaliableClasses.get(type) + " ";
                write.write(s.getBytes(StoreableMain.CODING));
            }
        }
        tables.put(name, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String name) throws IOException {
        checkTableName(name);
        if (tables.get(name) == null) {
            throw new IllegalStateException(name + " doesn't exist");
        } else {
            Path tableDir = dbPath.resolve(name);
            Table removedTable = tables.remove(name);
            if (removedTable == null) {
                throw new IllegalStateException("There is no such table");
            } else {
                recoursiveDelete(tableDir.toFile());
            }
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        value = value.trim();
        if ((value.charAt(0) != '[') || (value.charAt(value.length() - 1) != ']')){
            throw new ParseException("'[' or ']' is missing", 0);
        }
        value = value.substring(1, value.length() - 1);
        String[] records = value.split("," + StoreableMain.IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX);
        if (records.length != table.getColumnsCount()) {
            throw new ParseException("Incorrect number of records: "
                    + table.getColumnsCount() + " expected, but " + records.length + " found.", 0);
        }
        Storeable deserialized = createFor(table);
        for (int i = 0; i < records.length; i++) {
            String record = records[i].trim();
            try {
                if (record.equals("null")) {
                    deserialized.setColumnAt(i, null);
                } else {
                    deserialized.setColumnAt(i, StoreableMain.parseTypes.get(table.getColumnType(i)).apply(record));
                }
            } catch (ColumnFormatException e) {
                throw new ParseException("Record " + i + " is not correct: " + e.getMessage(), 0);
            } catch (NumberFormatException e) {
                throw new ParseException("Record " + i + " is not correct: " + record + " is not "
                        + table.getColumnType(i).getSimpleName(), 0);
            }
        }
        return deserialized;
    }

    @Override
    public String serialize(Table table, Storeable value) {
        List<String> storeableValues = getStoreableValues(table, value);
        String joined = String.join(", ", storeableValues);
        StringBuilder result = new StringBuilder();
        result.append('[');
        result.append(joined);
        result.append(']');
        return result.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        int size = table.getColumnsCount();
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            types.add(table.getColumnType(i));
        }
        return new StoreableImpl(types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        Storeable record = createFor(table);
        int size = table.getColumnsCount();
        if (values.size() != size) {
            throw new IndexOutOfBoundsException("wrong amount of columns");
        } else {
            for (int i = 0; i < size; i++) {
                Object currentValue = values.get(i);
                record.setColumnAt(i, currentValue);
            }
            return record;
        }
    }

    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        for (Map.Entry<String, Table> entry : tables.entrySet()) {
            if (!(entry.getValue() == null)) {
                tableNames.add(entry.getKey());
            }
        }
        return tableNames;
    }

    private static void checkTableName(final String name) {
        if (name == null || name.matches(StoreableMain.ILLEGAL_TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid format: " + name);
        }
    }

    public static List<String> getStoreableValues(Table table, Storeable value) {
        int tableColumnsAmount = table.getColumnsCount();
        List<String> storeableValues = new ArrayList<>();
        Method[] methods = value.getClass().getDeclaredMethods();
        Map<Class<?>, Method> getMethods = new HashMap<>();
        for (Method method: methods) {
            getMethods.put(method.getReturnType(), method);
        }
        for (int i = 0; i < tableColumnsAmount; i++) {
            Class currentColumnType = table.getColumnType(i);
            try {
                Object columnValue = getMethods.get(currentColumnType).invoke(value, i);
                if (columnValue == null) {
                    storeableValues.add("null");
                } else {
                    if (columnValue.getClass().equals(String.class)) {
                        storeableValues.add(String.valueOf('"') + columnValue.toString() + '"');
                    } else {
                        storeableValues.add(String.valueOf(columnValue));
                    }
                }
            } catch (InvocationTargetException e) {
                throw new ColumnFormatException(e.getTargetException().getMessage());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        return storeableValues;
    }

    private static void recoursiveDelete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File currentFile : file.listFiles()) {
                recoursiveDelete(currentFile);
            }
        }
        if (!file.delete()) {
            throw new IOException("Unable to delete: " + file);
        }
    }
}
