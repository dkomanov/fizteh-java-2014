package ru.fizteh.fivt.students.dnovikov.storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.TableNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBaseProvider implements TableProvider {
    private Path rootDirectory;

    private ArrayList<Table> tables = new ArrayList<>();
    private Map<String, Table> tableNames = new TreeMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public DataBaseProvider(String directoryPath) throws LoadOrSaveException {
        if (directoryPath == null) {
            throw new IllegalArgumentException("database directory not set");
        } else {
            rootDirectory = new File(directoryPath).toPath();
        }
        loadTables();
    }

    public Path getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("cannot get table: null");
        }
        lock.readLock().lock();
        try {
            return tableNames.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("cannot create table");
        }
        lock.writeLock();
        try {
            if (tableNames.containsKey(name)) {
                return null;
            } else {
                try {
                    Path newDir = rootDirectory.resolve(name);
                    Files.createDirectory(newDir);
                    writeSignature(newDir, columnTypes);
                } catch (IOException e) {
                    throw new LoadOrSaveException("can't create directory: " + rootDirectory + File.separator + name);
                } catch (InvalidPathException e) {
                    throw new LoadOrSaveException("invalid path");
                }
                DataBaseTable table = new DataBaseTable(name, this);
                tableNames.put(name, table);
                tables.add(table);
                return table;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void writeSignature(Path tableDirectory, List<Class<?>> columnTypes) throws LoadOrSaveException {
        Path signaturePath = tableDirectory.resolve(Utils.SIGNATURE_FILE_NAME);
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("list of column types can't be null or empty");
        }

        try (PrintWriter printer = new PrintWriter(signaturePath.toString())) {
            StringBuilder typesStringBuilder = new StringBuilder();
            for (Class<?> typeClass : columnTypes) {
                typesStringBuilder.append(Utils.SUPPORTED_TYPES_TO_NAMES.get(typeClass));
                typesStringBuilder.append(" ");
            }
            typesStringBuilder.deleteCharAt(typesStringBuilder.length() - 1);
            printer.print(typesStringBuilder.toString());
        } catch (IOException e) {
            throw new LoadOrSaveException("Unable write signature to " + signaturePath.toString());
        }
    }

    @Override
    public void removeTable(String name) throws TableNotFoundException, LoadOrSaveException {
        if (name == null) {
            throw new IllegalArgumentException("cannot get table: name should be non-null string");
        }
        lock.writeLock().lock();
        try {
            DataBaseTable table = (DataBaseTable) tableNames.get(name);
            if (table == null) {
                throw new TableNotFoundException();
            } else {
                tableNames.remove(name);
                table.drop();
                tables.remove(table);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null) {
            return null;
        }
        if (!value.startsWith("[")) {
            throw new ParseException("can't deserialize '" + value + "': argument doesn't start with '['", 0);
        }

        if (!value.endsWith("]")) {
            throw new ParseException("can't deserialize '" + value + "': argument doesn't end with ']'", 0);
        }

        Class<?>[] types = new Class<?>[table.getColumnsCount()];
        for (int i = 0; i < types.length; i++) {
            types[i] = table.getColumnType(i);
        }
        StoreableType storeableValue = new StoreableType(Arrays.asList(types));
        value = value.substring(value.indexOf('[') + 1, value.lastIndexOf(']'));
        String[] values = value.split(Utils.REGEXP_TO_SPLIT_JSON);
        if (values.length != types.length) {
            throw new ColumnFormatException("Wrong number of values");
        }
        for (int i = 0; i < types.length; i++) {
            values[i] = values[i].trim();
            if (values[i].equals("null")) {
                storeableValue.setColumnAt(i, null);
            } else if (!types[i].equals(Integer.class) && !types[i].equals(String.class)) {
                try {
                    storeableValue.setColumnAt(i,
                            types[i].getMethod("parse" + types[i].getSimpleName(),
                                    String.class).invoke(null, values[i]));
                } catch (NoSuchMethodException | InvocationTargetException
                        | IllegalArgumentException | IllegalAccessException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            } else if (types[i].equals(Integer.class)) {
                try {
                    storeableValue.setColumnAt(i, Integer.parseInt(values[i]));
                } catch (NumberFormatException e) {
                    throw new ParseException("can't parse to Integer: " + e.getMessage(), 0);
                }
            } else if (types[i].equals(String.class)) {
                try {
                    storeableValue.setColumnAt(i,
                            values[i].substring(values[i].indexOf('"') + 1,
                                    values[i].lastIndexOf('"')));
                } catch (IndexOutOfBoundsException e) {
                    throw new ParseException("wrong JSON format for String", 0);
                }
            }
        }
        return storeableValue;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int size = table.getColumnsCount();
        builder.append('[');
        Object obj;

        for (int i = 0; i < size; i++) {
            obj = value.getColumnAt(i);
            if (obj != null && obj.getClass().equals(String.class)) {
                builder.append('"');
            }
            builder.append(obj);
            if (obj != null && obj.getClass().equals(String.class)) {
                builder.append('"');
            }
            if (i != size - 1) {
                builder.append(", ");
            }
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        int size = table.getColumnsCount();
        List<Class<?>> types = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            types.add(table.getColumnType(i));
        }
        return new StoreableType(types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        Class<?>[] types = new Class[table.getColumnsCount()];
        for (int i = 0; i < types.length; i++) {
            types[i] = table.getColumnType(i);
        }

        StoreableType storeableValue = new StoreableType(Arrays.asList(types));
        for (int i = 0; i < values.size(); i++) {
            storeableValue.setColumnAt(i, values.get(i));
        }
        return storeableValue;
    }

    @Override
    public List<String> getTableNames() {
        List<String> result = new ArrayList<>();
        for (Table table : tables) {
            String tableName = table.getName();
            result.add(tableName);
        }
        return result;
    }

    public List<TableInfo> showTable() {
        List<TableInfo> result = new ArrayList<>();
        for (Table table : tables) {
            String tableName = table.getName();
            int size = table.size();
            result.add(new TableInfo(tableName, size));
        }
        return result;
    }

    public void loadTables() throws LoadOrSaveException {
        if (rootDirectory.toFile().isDirectory()) {
            File[] foldersInRoot = rootDirectory.toFile().listFiles();
            if (foldersInRoot == null) {
                throw new LoadOrSaveException("can't load database");
            }

            for (File folder : foldersInRoot) {
                if (!folder.isDirectory()) {
                    throw new LoadOrSaveException("file '" + folder.getName() + "' in root directory");
                }
            }

            for (File folder : foldersInRoot) {
                DataBaseTable currentTable = new DataBaseTable(folder.getName(), this);
                tables.add(currentTable);
                tableNames.put(folder.getName(), currentTable);
            }
        } else if (!rootDirectory.toFile().exists()) {
            throw new LoadOrSaveException("root directory '" + rootDirectory.getFileName() + "' not found");
        } else {
            throw new LoadOrSaveException("root directory '" + rootDirectory.getFileName() + "' is not directory");
        }
    }
}
