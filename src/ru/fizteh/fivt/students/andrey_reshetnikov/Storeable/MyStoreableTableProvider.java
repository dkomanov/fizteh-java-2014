package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.JUnit.HybridTable;
import ru.fizteh.fivt.students.andrey_reshetnikov.JUnit.JUnitDataBaseDir;
import ru.fizteh.fivt.students.andrey_reshetnikov.JUnit.MyTableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.ConstClass;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyStoreableTableProvider implements TableProvider {

    private HashMap<String, Table> tables;
    private HashMap<String, File> signatures; // Name of table -> path to signature
    private MyTableProvider oldProvider;
    private File mainDirectory;
    private String using;

    private String readSignature(File signature) throws IOException {
        Charset charset = Charset.forName(ConstClass.MY_FORMAT);
        Path path = signature.toPath();
        String line;
        try (BufferedReader stream = Files.newBufferedReader(path, charset)) {
            line = stream.readLine();
        }
        if (line == null) {
            throw new IOException("Empty signature file");
        }
        return line;
    }

    private void writeSignature(File signature, String line) throws IOException {
        Charset charset = Charset.forName(ConstClass.MY_FORMAT);
        Path path = signature.toPath();
        try (BufferedWriter stream = Files.newBufferedWriter(path, charset)) {
            stream.write(line);
        }
    }

    protected MyStoreableTableProvider(String path) throws IOException {
        try {
            oldProvider = new MyTableProvider(path);
            JUnitDataBaseDir unstructuredDbDir = oldProvider.getJUnitDir();
            mainDirectory = new File(path);
            tables = new HashMap<>();
            signatures = new HashMap<>();
            for (Map.Entry<String, HybridTable> entry: unstructuredDbDir.tables.entrySet()) {
                File signature = new File(entry.getValue().getCleanTable().mainDir, "signature.tsv");
                if (!signature.exists()) {
                    throw new IOException("No signature file specified in table " + entry.getKey());
                }
                signatures.put(entry.getKey(), signature);
                String typesInString = readSignature(signature);
                List<Class<?>> types = TypeTransformer.classListFromString(typesInString);
                MyStoreableTable table = new MyStoreableTable(entry.getValue(), types, entry.getKey(), this);
                tables.put(entry.getKey(), table);
            }
        } catch (Exception e) {
            throw new IOException("Problems while reading from database directory");
        }
    }

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
        try {
            oldProvider.createTable(name);
        } catch (Exception e) {
            throw new IOException("cannot create table directory");
        }
        HybridTable hybrid = oldProvider.getJUnitDir().tables.get(name);
        String typesInString = TypeTransformer.stringFromClassList(columnTypes);
        File signature = new File(new File(mainDirectory, name), "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IOException("cannot create signature file");
        }
        writeSignature(signature, typesInString);
        Table table = new MyStoreableTable(hybrid, columnTypes, name, this);
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
            throw new IOException();
        }
        try {
            oldProvider.removeTable(name);
        } catch (Exception e) {
            throw new IOException();
        }
        tables.remove(name);
        signatures.remove(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> types = ((MyStoreableTable) table).types;
        try {
            List<Object> values =  XmlSerializer.deserializeString(value, types);
            return createFor(table, values);
        } catch (ParserConfigurationException e) {
            throw new ParseException("Error in parser configuration", 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        int expected = table.getColumnsCount();
        int found = ((StoreableValue) value).getValues().size();
        if (expected != found) {
            throw new ColumnFormatException("Incorrect number of values to serialize for table "
                    + table.getName() + ": " + expected
                    + " expected, " + ((StoreableValue) value).getValues().size() + " found");
        }
        for (int i = 0; i < table.getColumnsCount(); i++) {
            Class own = table.getColumnType(i);
            Class passed = ((StoreableValue) value).getColumnType(i);
            String representation;
            if (value.getColumnAt(i) == null) {
                representation = "null";
            } else {
                representation = value.getColumnAt(i).toString();
            }
            if (own != passed) {
                throw new ColumnFormatException("Column format on position " + i + " is "
                        + own.getSimpleName() + ". " + representation + " is " + passed.getSimpleName());
            }
        }
        return XmlSerializer.serializeObjectList(((StoreableValue) value).getValues());
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableValue(((MyStoreableTable) table).types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        StoreableValue result = new StoreableValue(((MyStoreableTable) table).types);
        for (int i = 0; i < values.size(); i++) {
            result.setColumnAt(i, values.get(i));
        }
        return result;
    }

    @Override
    public List<String> getTableNames() {
        List<String> res = new ArrayList<>();
        res.addAll(tables.keySet());
        return res;
    }

    public Table setUsing(String name) {
        if (!tables.containsKey(name)) {
            return null;
        } else {
            using = name;
            return tables.get(using);
        }
    }

    public Table getUsing() {
        if (using == null) {
            return null;
        }
        return getTable(using);
    }
}
