package ru.fizteh.fivt.students.torunova.storeable.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.TableNotCreatedException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by nastya on 20.11.14.
 */
public class DatabaseWrapper implements TableProvider {
    private Database db;
    private static final String SIGNATURE_FILE = "signature.tsv";
    private static final String REGEXP_TO_SPLIT_JSON = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
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
        File table = new File(db.getDbName(), name);
        File signature = new File(table, SIGNATURE_FILE);
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream(signature));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Class<?>> types =  new ArrayList<>();
        while (scanner.hasNext()) {
            types.add(classForName(scanner.next()));
        }
        try {
            return new TableWrapper(db.getTable(name), this, types.toArray(new Class[0]));
        } catch (TableNotCreatedException | IOException | IncorrectFileException e) {
            throw  new RuntimeException(e);
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        TableImpl t = db.createTable(name);
        if (t == null) {
            return null;
        }
        TableWrapper table;
        try {
            table = new TableWrapper(t, this, columnTypes.toArray(new Class[0]));
        } catch (TableNotCreatedException | IncorrectFileException e) {
            throw new RuntimeException(e);
        }
        File tableDir = new File(db.getDbName(), name);
        File signature = new File(tableDir, SIGNATURE_FILE);
        signature.createNewFile();
        PrintWriter fos = new PrintWriter(new FileOutputStream(signature));
        for (Class<?> type: columnTypes) {
            fos.print(normalSimpleName(type) +  " ");
        }
        fos.flush();
        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {
        db.removeTable(name);
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
        checkJsonFormat(value);

        value = value.substring(value.indexOf('[') + 1, value.lastIndexOf(']'));
        value = value.trim();
        String[] values = Stream.of(value.split(REGEXP_TO_SPLIT_JSON)).
                map(s -> s.trim()).toArray(size -> new String[size]);
        if (values.length != types.length) {
            throw new ColumnFormatException("Wrong number of values");
        }
        for (int i = 0; i < types.length; i++) {
            try {
                if (values[i].equals("null")) {
                    storeableValue.setColumnAt(i, null);
                } else if (!types[i].equals(Integer.class)
                        && !types[i].equals(String.class)
                        && !types[i].equals(Boolean.class)) {
                    try {

                        storeableValue.setColumnAt(i,
                                types[i].getMethod("parse" + types[i].getSimpleName(),
                                        String.class).invoke(null, values[i]));
                    } catch (NoSuchMethodException
                            | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        Throwable exception = e.getTargetException();
                        if (exception.getClass() == NumberFormatException.class) {
                            throw new IllegalArgumentException("column " + (i + 1)
                                    + " should contain " + classForName(types[i].getSimpleName()));
                        } else {
                            throw new RuntimeException(exception);
                        }

                    }
                } else if (types[i].equals(Integer.class)) {
                    storeableValue.setColumnAt(i, Integer.parseInt(values[i]));
                } else if (types[i].equals(String.class)) {
                    if (!values[i].contains("\"")) {
                        throw new IllegalArgumentException("column " + (i + 1) + " should contain string");
                    }
                    storeableValue.setColumnAt(i,
                            values[i].substring(values[i].indexOf('"') + 1,
                                    values[i].lastIndexOf('"')));
                } else if (types[i].equals(Boolean.class)) {
                    if (!Boolean.parseBoolean(values[i]) && !values[i].equals("false")) {
                        throw new IllegalArgumentException("column " + (i + 1) + " should contain boolean");
                    }
                    storeableValue.setColumnAt(i, Boolean.parseBoolean(values[i]));
                }
            } catch (IllegalArgumentException e) {
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
        return new ArrayList<>(db.showTables().keySet());
    }
    public Map<String, Integer> showTables() {
        return db.showTables();
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
    private void checkJsonFormat(String value) {
        if (!value.startsWith("[") || !value.endsWith("]")) {
            throw new IllegalStateException("wrong value format "
                    + "(it should be like this: [value1, value2,..., valueN])");
        }
    }
}
