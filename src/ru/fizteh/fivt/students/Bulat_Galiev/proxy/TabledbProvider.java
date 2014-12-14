package ru.fizteh.fivt.students.Bulat_Galiev.proxy;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class TabledbProvider implements TableProvider, AutoCloseable {
    private Map<String, Tabledb> tableMap;
    private Path tablesDirPath;
    private Table currentTable;
    private Types serializer;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    boolean closed;

    public TabledbProvider(final String dir) {
        try {
            tablesDirPath = Paths.get(dir);
            if (!Files.exists(tablesDirPath)) {
                tablesDirPath.toFile().mkdir();
            }
            if (!tablesDirPath.toFile().isDirectory()) {
                throw new IllegalArgumentException("Incorrect path.");
            }
            currentTable = null;
            tableMap = new HashMap<>();
            serializer = new Types();
            String[] tablesDirlist = tablesDirPath.toFile().list();
            for (String curTableDir : tablesDirlist) {
                Path curTableDirPath = tablesDirPath.resolve(curTableDir);
                if (curTableDirPath.toFile().isDirectory()) {
                    Table curTable = new Tabledb(curTableDirPath, curTableDir,
                            this, null);
                    tableMap.put(curTableDir, (Tabledb) curTable);
                } else {
                    throw new IllegalArgumentException(
                            "Directory contains non-directory files.");
                }
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("directory name " + dir
                    + " is incorrect. " + e.getMessage());
        }
    }

    void isClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("database is closed");
        }
    }

    public void changeCurTable(final String name) {
        isClosed();
        lock.writeLock().lock();
        try {
            if (name != null && !name.equals("")) {
                tablesDirPath.resolve(name);
                Table newTable = tableMap.get(name);
                if (newTable != null) {
                    currentTable = newTable;
                } else {
                    throw new IllegalStateException(name + " does not exist");
                }
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Table createTable(final String name, final List<Class<?>> columnTypes) {
        isClosed();
        try {
            if (name != null && !name.equals("")) {
                if (tableMap.get(name) != null) {
                    return null;
                }

                if (columnTypes == null) {
                    throw new IllegalArgumentException("columnTypes is null");
                }

                if (columnTypes.isEmpty()) {
                    throw new IllegalArgumentException(
                            "ColumnTypes list is empty");
                }
                Path newTablePath = tablesDirPath.resolve(name);
                if (!newTablePath.toFile().mkdir()) {
                    throw new IllegalArgumentException(
                            "Error creating directory " + name);
                }
                Path signature = newTablePath.resolve("signature.tsv");
                try {
                    if (!signature.toFile().createNewFile()) {
                        throw new IllegalArgumentException(
                                "signature making error");
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error creating table "
                            + name + ": " + e.getMessage());
                }

                FileOutputStream fileOutputStream;
                try {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (Class<?> type : columnTypes) {
                        if (type == null) {
                            throw new IllegalArgumentException(
                                    "wrong column type");
                        }
                        String typeString = Types.classToString(type);
                        if (typeString == null) {
                            throw new IllegalArgumentException(
                                    "wrong column type");
                        }
                        stringBuilder.append(type);
                        stringBuilder.append(' ');
                    }

                    String typeString = stringBuilder.toString();

                    typeString = typeString.substring(0,
                            typeString.length() - 1);
                    fileOutputStream = new FileOutputStream(signature.toFile());
                    fileOutputStream.getChannel().truncate(0);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                            fileOutputStream);
                    DataOutputStream dataOutputStream = new DataOutputStream(
                            bufferedOutputStream);
                    dataOutputStream.write(typeString.getBytes("UTF-8"));
                    dataOutputStream.close();

                } catch (IOException e) {
                    throw new IllegalArgumentException("Error creating table "
                            + name + ": " + e.getMessage());
                }

                Table newTable = new Tabledb(newTablePath, name, this,
                        columnTypes);
                tableMap.put(name, (Tabledb) newTable);
                return newTable;
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        }
    }

    public Table createStoreableTable(final String[] arguments) {
        isClosed();
        String[] types = Arrays.copyOfRange(arguments, 1, arguments.length);
        String name = arguments[0];

        List<Class<?>> listOfClasses = new ArrayList<Class<?>>();
        for (String string : types) {
            if (Types.stringToClass(string) == null) {
                throw new IllegalArgumentException("Wrong type \"" + string
                        + "\".");
            }
            listOfClasses.add(Types.stringToClass(string));
        }
        return this.createTable(name, listOfClasses);
    }

    @Override
    public void removeTable(final String name) throws IOException {
        isClosed();
        try {
            if (name != null && !name.equals("")) {
                tablesDirPath.resolve(name);
                Table removedTable = tableMap.remove(name);
                if (removedTable == null) {
                    throw new IllegalStateException(name + " does not exist");
                } else {
                    if (currentTable == removedTable) {
                        currentTable = null;
                    }
                    ((Tabledb) removedTable).deleteTable();
                }
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        }
    }

    public Table getDataBase() {
        isClosed();
        return currentTable;
    }

    @Override
    public Table getTable(final String name) {
        isClosed();
        lock.writeLock().lock();
        try {
            if (name != null && !name.trim().equals("")) {
                tablesDirPath.resolve(name);
                Table singleTable = tableMap.get(name);

                if (singleTable != null) {
                    checkTable(singleTable);
                    try {
                        ((Tabledb) singleTable).isClosed();
                    } catch (IllegalStateException e) {
                        Path newTablePath = tablesDirPath.resolve(name);
                        singleTable = new Tabledb(newTablePath, name, this,
                                null);
                        tableMap.put(name, (Tabledb) singleTable);
                    }
                }

                return singleTable;
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void checkTable(final Table singleTable) {
        isClosed();
        lock.readLock().lock();
        try {
            try {
                Path tempDirPath = tablesDirPath;
                Scanner scanner;
                scanner = new Scanner(tempDirPath
                        .resolve(singleTable.getName())
                        .resolve("signature.tsv"));
                String typesString = scanner.nextLine();
                checkColumnTypes(typesString);
                scanner.close();
            } catch (IOException | IllegalArgumentException e) {
                throw new IllegalArgumentException("table named "
                        + singleTable.getName() + " is incorrect: "
                        + e.getMessage());
            }

            List<String> keys = singleTable.list();
            for (String key : keys) {
                singleTable.get(key);
            }
        } finally {
            lock.readLock().unlock();
        }

    }

    public static void checkColumnTypes(final String oldTypesString) {
        String typesString = "";
        String prefix = "class java.lang.";
        if (oldTypesString.startsWith(prefix)) {
            typesString = oldTypesString.substring(prefix.length());
        } else {
            throw new IllegalArgumentException("Wrong signature.tsv format.");
        }
        String[] types = typesString.split(" class java.lang.");
        for (String type : types) {
            if (!type.equals("String")) {
                type = Character.toLowerCase(type.charAt(0))
                        + type.substring(1);
            }
            if (type.equals("integer")) {
                type = "int";
            }
            if (Types.stringToClass(type) == null) {
                throw new IllegalArgumentException("Class " + type
                        + " is not supported.");
            }
        }
    }

    @Override
    public Storeable deserialize(final Table table, final String value)
            throws ParseException {
        isClosed();
        return serializer.deserialize(table, value);
    }

    @Override
    public String serialize(final Table table, final Storeable value) {
        isClosed();
        return serializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(final Table table) {
        isClosed();
        if (table == null) {
            throw new IllegalArgumentException("null table");
        }
        List<Object> values = new ArrayList<>(table.getColumnsCount());

        return new Storeabledb(values);
    }

    @Override
    public Storeable createFor(final Table table, final List<?> values) {
        isClosed();
        if (table == null || values == null || values.isEmpty()) {
            throw new IllegalArgumentException("null value or table");
        }

        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("invalid values count");
        }
        List<Object> objectValues = new ArrayList<>(values);

        return new Storeabledb(objectValues);
    }

    @Override
    public List<String> getTableNames() {
        isClosed();
        return new ArrayList<String>(tableMap.keySet());
    }

    public Set<String> getKeySet() {
        isClosed();
        return tableMap.keySet();
    }

    @Override
    public void close() {
        for (Entry<String, Tabledb> entry : tableMap.entrySet()) {
            entry.getValue().close();
        }
    }

}
