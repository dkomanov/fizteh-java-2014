package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

import org.json.JSONArray;
import org.json.JSONException;

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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lenovo on 09.11.2014.
 */
public class MyTableProvider implements TableProvider {

    HashMap<String, MyTable> tables;
    HashMap<String, File> signatures;
    File mainDirectory;
    MyTable usingTable;

    private static final HashMap<Class, Deserializer> ReadMap;

    static {
        ReadMap = new HashMap<>();
        ReadMap.put(Integer.class, Integer::valueOf);
        ReadMap.put(Long.class, Long::valueOf);
        ReadMap.put(Byte.class, Byte::valueOf);
        ReadMap.put(Double.class, Double::valueOf);
        ReadMap.put(Float.class, Float::valueOf);
        ReadMap.put(Boolean.class, string -> {
            if (string.equals("true")) {
                return true;
            }
            if (string.equals("false")) {
                return false;
            }
            throw new ParseException("Wrong boolean type", 0);
        });
        ReadMap.put(String.class, string -> {
            return string;
        });
    }

    private String readSignature(File signature) throws IOException {
        Charset charset = Charset.forName("UTF-8");
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

    protected void writeSignature(File signature, String line) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        Path path = signature.toPath();
        try (BufferedWriter stream = Files.newBufferedWriter(path, charset)) {
            stream.write(line);
        }
    }

    protected MyTableProvider(String path) throws IOException {
        mainDirectory = new File(path);
        if (!mainDirectory.isDirectory()) {
            throw new IOException(path.toString() + " is not a directory");
        }
        tables = new HashMap<>();
        signatures = new HashMap<>();
        File[] files = mainDirectory.listFiles();
        try {
            for (File tableDirectory: files) {
                File signature = new File(tableDirectory, "signature.tsv");
                String name = tableDirectory.getName();
                if (!signature.exists()) {
                    throw new IOException("No signature file specified in table " + name);
                }
                signatures.put(name, signature);
                String typesInString = readSignature(signature);
                List<Class<?>> types = TypeTransformer.typeListFromString(typesInString);
                FileMap filemap = new FileMap(tableDirectory);
                MyTable table = new MyTable(path, filemap, types, this, tableDirectory.toPath());
                tables.put(name, table);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return tables.get(name);
    }

    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        }

        CreateCommand create = new CreateCommand();
        List<String> list = new LinkedList<String>();
        list.add("create");
        list.add(name);
        list.addAll(TypeTransformer.stringListFromTypeList(columnTypes));
        String[] args = list.toArray(new String[0]);
        try {
            create.execute(args, this);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return tables.get(name);
    }

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
        DropCommand drop = new DropCommand();
        String[] args = {"drop", name};
        try {
            drop.execute(args, this);
        } catch (Exception e) {
            throw new IOException();
        }
        tables.remove(name);
        signatures.remove(name);
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> types = ((MyTable) table).types;
        List<Object> values = new LinkedList<Object>();
        if (value == null) {
            return null;
        }

        JSONArray array;
        try {
            array = new JSONArray(value);
            for (int i = 0; i < array.length(); ++i) {
                try {
                    if (array.get(i).equals(null)) {
                        values.add(null);
                    } else {
                        values.add(ReadMap.get(table.getColumnType(i)).getObject(array.get(i).toString()));
                    }
                } catch (ClassCastException e) {
                    throw new ParseException(array.get(i).toString(), 0);
                }
            }
        } catch (JSONException e) {
            throw new ParseException("cannot parse string: " + value, 0);
        }
        return createFor(table, values);
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        int expected = table.getColumnsCount();
        int found = ((MyStoreable) value).getValues().size();
        if (expected != found) {
            throw new ColumnFormatException("Incorrect number of values to serialize for table "
                    + table.getName() + ": " + expected
                    + " expected, " + ((MyStoreable) value).getValues().size() + " found");
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Class own = table.getColumnType(i);
            Class passed = ((MyStoreable) value).getColumnType(i);
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
        JSONArray array = new JSONArray(((MyStoreable) value).getValues());
        return array.toString();
    }

    public Storeable createFor(Table table) {
        return new MyStoreable(((MyTable) table).types);
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        MyStoreable result = new MyStoreable(((MyTable) table).types);
        for (int i = 0; i < values.size(); i++) {
            result.setColumnAt(i, values.get(i));
        }
        return result;
    }

    public List<String> getTableNames() {
        List<String> list = new ArrayList<>();
        list.addAll(tables.keySet());
        return list;
    }

    interface Deserializer {
        Object getObject(String string) throws ParseException;
    }
}
