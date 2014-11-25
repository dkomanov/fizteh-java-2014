package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Alex on 23.11.14.
 */
public class StoreableTableProvider implements TableProvider {

    private String dbPath;
    private Map<Class<?>, String> classNames;
    private Map<String, Class<?>> classesByNames;
    private Map<String, StoreableTable> tables;

    private void initNames() {
        classNames = new HashMap<>();
        classNames.put(Integer.class, "int");
        classNames.put(Long.class, "long");
        classNames.put(Byte.class, "byte");
        classNames.put(Float.class, "float");
        classNames.put(Double.class, "double");
        classNames.put(Float.class, "float");
        classNames.put(Boolean.class, "boolean");
        classNames.put(String.class, "String");

        classesByNames = new HashMap<>();
        for (Class<?> sign : classNames.keySet()) {
            String name = classNames.get(sign);
            classesByNames.put(name, sign);
        }
    }

    private void initProvider() {
        File dir = new File(dbPath);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                File signFile = new File(file.getAbsoluteFile() + File.separator + "signature.tsv");
                List<Class<?>> sign;
                try {
                    Scanner scanner = new Scanner(signFile);
                    sign = new ArrayList<>();
                    while (scanner.hasNext()) {
                        String value = scanner.next();
                        sign.add(classesByNames.get(value));
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Error with reading");
                }
                StoreableTable storeableTable = new StoreableTable(this, file.getName(), dbPath, sign);
                tables.put(file.getName(), storeableTable);
            }
        }
    }

    public StoreableTableProvider(String path) {
        dbPath = path;
        tables = new HashMap<>();
        initNames();
        initProvider();
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Wrong table name");
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Wrong table name");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("columnTypes shouldn't be null");
        }
        try {
            new StoreableEntry(columnTypes);
        } catch (ColumnFormatException e) {
            throw new IllegalArgumentException("Bad input signature");
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            String path = dbPath + File.separator + name;
            File tableFile = new File(path);
            if (!tableFile.mkdir()) {
                throw new IOException("Can't create new file");
            }
            File signatureFile = new File(path + File.separator + "signature.tsv");
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(signatureFile));
            for (Class<?> sign : columnTypes) {
                new Writer().writeWord(dataOutputStream, sign.getName());
            }
            StoreableTable table = new StoreableTable(this, name, dbPath, columnTypes);
            tables.put(name, table);
            return table;
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("name shouldn't be null");
        }
        if (tables.containsKey(name)) {
            StoreableTable table = tables.get(name);
            File tableFile = new File(dbPath + File.separator + name);
            table.removeData(tableFile);
            tables.remove(name);
        } else {
            throw new IllegalStateException("Bad table name");
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> types = ((StoreableTable) table).signature;
        List<Object> res = XmlRunner.deserializeString(value, types);
        return createFor(table, res);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        int expectedColumnNum = table.getColumnsCount();
        int foundColumnNum = ((StoreableEntry) value).getObjectList().size();
        if (expectedColumnNum != foundColumnNum) {
            throw new IllegalArgumentException("Incorrect number of values");
        }
        for (int i = 0; i < table.getColumnsCount(); i++) {
            Class element = table.getColumnType(i);
            Class passed = ((StoreableEntry) value).getColumnType(i);
            String result;
            if (value.getColumnAt(i) == null) {
                result = "null";
            } else {
                result = value.getColumnAt(i).toString();
            }
            if (element != passed) {
                throw new ColumnFormatException("Bad coliumn");
            }
        }
        return XmlRunner.serializeObjects(((StoreableEntry) value).getObjectList());
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableEntry(((StoreableTable) table).signature);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableEntry storeableEntry = new StoreableEntry(((StoreableTable) table).signature);
        for (int i = 0; i < values.size(); i++) {
            storeableEntry.setColumnAt(i, values.get(i));
        }
        return storeableEntry;
    }
}
