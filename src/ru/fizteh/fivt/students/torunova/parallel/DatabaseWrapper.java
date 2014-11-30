package ru.fizteh.fivt.students.torunova.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.TableNotCreatedException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by nastya on 20.11.14.
 */
public class DatabaseWrapper implements TableProvider {
    private Database db;
    private static final String SIGNATURE_FILE = "signature.tsv";
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public DatabaseWrapper(String dbName) throws IncorrectDbException,
                                                 IncorrectDbNameException,
                                                 IncorrectFileException,
                                                 TableNotCreatedException,
                                                 IOException {
        db = new Database(dbName);
    }
    DatabaseWrapper(ru.fizteh.fivt.storage.strings.TableProvider newDb) {
        db = (Database) newDb;
    }

    @Override
    public int hashCode() {
        return db.hashCode();
    }

    @ Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DatabaseWrapper)) {
            return false;
        }
        return db.equals(((DatabaseWrapper) obj).db);
    }

    @Override
    public Table getTable(String name) {
        readWriteLock.readLock().lock();
        try {
            File table = new File(db.getDbName(), name);
            File signature = new File(table, SIGNATURE_FILE);
            Scanner scanner;
            try {
                scanner = new Scanner(new FileInputStream(signature));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            List<Class<?>> types = new ArrayList<>();
            while (scanner.hasNext()) {
                types.add(classForName(scanner.next()));
            }
            return new TableWrapper(db.getTable(name), this, types.toArray(new Class[0]));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            TableImpl t = db.createTable(name);
            if (t == null) {
                return null;
            }
            TableWrapper table = new TableWrapper(t, this, columnTypes.toArray(new Class[0]));
            File tableDir = new File(db.getDbName(), name);
            File signature = new File(tableDir, SIGNATURE_FILE);
            signature.createNewFile();
            PrintWriter fos = new PrintWriter(new FileOutputStream(signature));
            for (Class<?> type : columnTypes) {
                fos.print(normalSimpleName(type) + " ");
            }
            fos.flush();
            return table;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            db.removeTable(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        Class<?>[] types = new Class<?>[table.getColumnsCount()];
        for (int i = 0; i < types.length; i++) {
            types[i] = table.getColumnType(i);
        }
        StoreableType storeableValue = new StoreableType(types);
        if (value == null) {
            return null;
        }
        value = value.substring(value.indexOf('[') + 1, value.lastIndexOf(']'));
        value = value.trim();
        String[] values = value.split("\\s*,\\s*");
        if (values.length != types.length) {
            throw new ColumnFormatException("Wrong number of values");
        }
        for (int i = 0; i < types.length; i++) {
            try {
                if (values[i].equals("null")) {
                    storeableValue.setColumnAt(i, null);
                } else if (!types[i].equals(Integer.class) && !types[i].equals(String.class)) {
                    try {
                        storeableValue.setColumnAt(i,
                                types[i].getMethod("parse" + types[i].getSimpleName(),
                                        String.class).invoke(null, values[i]));
                    } catch (NoSuchMethodException
                            | InvocationTargetException
                            | IllegalArgumentException
                            | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else if (types[i].equals(Integer.class)) {
                    storeableValue.setColumnAt(i, Integer.parseInt(values[i]));
                } else if (types[i].equals(String.class)) {
                    storeableValue.setColumnAt(i,
                            values[i].substring(values[i].indexOf('"') + 1,
                                    values[i].lastIndexOf('"')));
                }
            } catch (RuntimeException e) {
                throw new RuntimeException("wrong type( " + e.getMessage() + " )");
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
        Class<?>[] types = new Class[table.getColumnsCount()];
        for (int i = 0; i < types.length; i++) {
            types[i] = table.getColumnType(i);
        }
        return new StoreableType(types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException,
            IndexOutOfBoundsException {
        Class<?>[] types = new Class[table.getColumnsCount()];
        for (int i = 0; i < types.length; i++) {
            types[i] = table.getColumnType(i);
        }

        StoreableType storeableValue = new StoreableType(types);
        for (int i = 0; i < values.size(); i++) {
            storeableValue.setColumnAt(i, values.get(i));
        }
        return storeableValue;
    }

    @Override
    public List<String> getTableNames() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(db.showTables().keySet());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    public Map<String, Integer> showTables() {
        readWriteLock.readLock().lock();
        try {
            return db.showTables();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Class<?> classForName(String className) {
        switch(className) {
            case "int":
                return Integer.class;
            case "double":
                return Double.class;
            case "long":
                return Long.class;
            case "String":
                return String.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "boolean":
                return Boolean.class;
            default: throw new RuntimeException("Unsupported type " + className);
        }
    }
    private String normalSimpleName(Class<?> type) {
        String simpleName = type.getSimpleName();
        switch (simpleName) {
            case "Integer" :
                return "int";
            case "Float" :
                return "float";
            case "Double" :
                return "double";
            case "Long" :
                return "long";
            case "Boolean" :
                return "boolean";
            case "Byte" :
                return "byte";
            case "String" :
                return simpleName;
            default: throw new RuntimeException("Unsupported type " + simpleName);
        }
    }
}
