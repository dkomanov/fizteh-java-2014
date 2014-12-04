package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.EgorLunichkin.JUnit.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreableTableProvider implements TableProvider {
    public StoreableTableProvider(String dbDir) throws IOException {
        try {
            jTableProvider = new MyTableProvider(dbDir);
            dbPath = new File(dbDir);
            tables = new HashMap<>();
            signatures = new HashMap<>();
            for (String tableName : jTableProvider.tableNames()) {
                MyTable jTable = (MyTable) jTableProvider.getTable(tableName);
                File signature = new File(jTable.getTableDir(), "signature.tsv");
                if (!signature.exists()) {
                    throw new StoreableException("No signature file specified in table " + tableName);
                }
                signatures.put(tableName, signature);
                String types = readSignature(signature);
                List<Class<?>> typesList = TypeManager.getClasses(types);
                Table table = new StoreableTable(jTable, typesList, this);
                tables.put(tableName, table);
            }
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private HashMap<String, Table> tables;
    private HashMap<String, File> signatures;
    private MyTableProvider jTableProvider;
    private File dbPath;
    private String using;

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        }
        jTableProvider.createTable(name);
        MyTable jTable = (MyTable) jTableProvider.getTable(name);
        String types = TypeManager.getNames(columnTypes);
        File signature = new File(new File(dbPath, name), "signature.tsv");
        if (signature.createNewFile()) {
            throw new IOException("Cannot create signature file");
        }
        writeSignature(signature, types);
        Table table = new StoreableTable(jTable, columnTypes, this);
        tables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null || !tables.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        if (!signatures.get(name).delete()) {
            throw new IOException("Cannot delete signature file");
        }
        jTableProvider.removeTable(name);
        tables.remove(name);
        signatures.remove(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> typesList = ((StoreableTable) table).types;
        List<Object> values = XMLManager.deserialize(value, typesList);
        return this.createFor(table);
    }

    @Override
    public String serialize(Table table, Storeable value) {
        int expected = table.getColumnsCount();
        int found = ((StoreableEntry) value).values.size();
        if (expected != found) {
            throw new ColumnFormatException("Incorrect number of values to serialize");
        }
        for (int ind = 0; ind < table.getColumnsCount(); ++ind) {
            Class own = table.getColumnType(ind);
            Class given = ((StoreableEntry) value).types.get(ind);
            String typeName;
            if (value.getColumnAt(ind) == null) {
                typeName = "null";
            } else {
                typeName = value.getColumnAt(ind).toString();
            }
            if (own != given) {
                throw new ColumnFormatException("Column #" + ind + " has incorrect format");
            }
        }
        return XMLManager.serialize(((StoreableEntry) value).values);
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableEntry(((StoreableTable) table).types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        StoreableEntry entry = (StoreableEntry)this.createFor(table);
        for (int ind = 0; ind < values.size(); ++ind) {
            entry.setColumnAt(ind, values.get(ind));
        }
        return entry;
    }

    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        tableNames.addAll(tables.keySet());
        return tableNames;
    }

    private String readSignature(File signature) {
        return null;
    }

    private void writeSignature(File signature, String sign) {

    }
}
