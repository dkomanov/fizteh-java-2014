package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.EgorLunichkin.JUnit.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
                StoreableTable table = new StoreableTable(jTable, typesList, this);
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
        if (!signature.createNewFile()) {
            throw new IOException("Cannot create signature file");
        }
        writeSignature(signature, types);
        Table table = new StoreableTable(jTable, columnTypes, this);
        tables.put(name, table);
        signatures.put(name, signature);
        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException();
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
        try {
            List<Object> values = XMLManager.deserialize(value, typesList);
            return createFor(table, values);
        } catch (ParserConfigurationException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
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
            if (own != given) {
                throw new ColumnFormatException("Column #" + ind + " has incorrect format");
            }
        }
        try {
            return XMLManager.serialize(((StoreableEntry) value).values);
        } catch (StoreableException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableEntry(((StoreableTable) table).types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        StoreableEntry entry = (StoreableEntry) this.createFor(table);
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

    private String readSignature(File signature) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        Path path = signature.toPath();
        String inputSignature;
        try (BufferedReader inputStream = Files.newBufferedReader(path, charset)) {
            inputSignature = inputStream.readLine();
        }
        if (inputSignature == null) {
            throw new IOException("Empty signature file");
        }
        return inputSignature;
    }

    private void writeSignature(File signature, String sign) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        Path path = signature.toPath();
        try (BufferedWriter outputStream = Files.newBufferedWriter(path, charset)) {
            outputStream.write(sign);
        }
    }

    public Table getUsing() {
        if (using == null) {
            return null;
        }
        return tables.get(using);
    }

    public Table setUsing(String name) {
        if (!tables.containsKey(name)) {
            return null;
        }
        using = name;
        return tables.get(using);
    }
}
