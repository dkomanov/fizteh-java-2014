package ru.fizteh.fivt.students.ryad0m.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTableProvider implements TableProvider {
    private Path location;
    private HashMap<String, StructedTable> tables;


    public MyTableProvider(Path path) throws IOException {
        location = path;
        tables = new HashMap<String, StructedTable>();
        if (!location.toFile().exists()) {
            location.toFile().mkdirs();
        }
        if (!location.toFile().isDirectory()) {
            throw new IOException("it isn't a directory");
        }
        File[] tableDirs = location.toFile().listFiles();
        for (File table : tableDirs) {
            if (table.exists() && table.isDirectory()) {
                StructedTable newDirTable = new StructedTable(table.toPath());
                tables.put(table.getName(), newDirTable);
            }
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return new TransactionTable(tables.get(name));
        } else {
            return null;
        }
    }

    private boolean checkName(String s) {
        if (s == null) {
            return false;
        }
        return !s.contains("/");
    }


    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!checkName(name)) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        }
        StructedTable t = new StructedTable(location.resolve(name).normalize(), columnTypes);
        tables.put(name, t);
        return new TransactionTable(t);
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException();
        }
        tables.get(name).deleteData();
        tables.remove(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Class<?>> list = ((TransactionTable) table).getStructedTable().getColumnTypes();
        return new MyStorable(list, XmlSerializer.deserializeString(value, list));
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        ((TransactionTable) table).getStructedTable().checkIntegrity(value);
        return XmlSerializer.serializeObjectList(((MyStorable) value).getValues());
    }

    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> list = ((TransactionTable) table).getStructedTable().getColumnTypes();
        return new MyStorable(list);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> list = ((TransactionTable) table).getStructedTable().getColumnTypes();
        return new MyStorable(list, values);
    }

    @Override
    public List<String> getTableNames() {
        return new ArrayList<String>(tables.keySet());
    }
}
