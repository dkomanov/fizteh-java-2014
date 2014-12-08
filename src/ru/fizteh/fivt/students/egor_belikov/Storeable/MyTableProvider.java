package ru.fizteh.fivt.students.egor_belikov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTableProvider implements TableProvider {

    public static String currentPath;
    public static Map<String, Table> listOfTables;
    public static MyTable currentTable;

    public MyTableProvider(String p) {
        if (p == null) {
            throw new IllegalArgumentException("MyTableProvider constructor: Null path");
        }
        currentPath = p;
        listOfTables = new HashMap<>();
        currentTable = null;
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return listOfTables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("Directory name or types of columns is null");
        }
        String pathToTable = currentPath + File.separator + name;
        File table = new File(pathToTable);
        if (table.exists() && table.isDirectory()) {
            return null;
        } else {
            Table t = new MyTable(name, currentPath, columnTypes);
            if (!table.mkdir()) {
                System.err.println("Unable to create a table");
            }
            return t;
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Name table is null");
        }
        String pathToTable = currentPath + File.separator + name;
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            throw new IllegalStateException("Table not exist");
        } else {
            if (currentTable != null) {
                if (currentTable.getName().equals(name)) {
                    currentTable = null;
                }
            }
            MyTable t = (MyTable) listOfTables.get(name);
            listOfTables.remove(name);
            t.rm();
            table.delete();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return MySerializer.deserialize(table, value, ((MyTable) table).signature);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        MySerializer ts = new MySerializer();
        return ts.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        List<Object> lo = new ArrayList<>();
        return new MyStoreable(lo, ((MyTable) table).signature);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Object> lo = new ArrayList<>(values);
        return new MyStoreable(lo, ((MyTable) table).signature);
    }

    @Override
    public List<String> getTableNames() {
        return new ArrayList<>(listOfTables.keySet());
    }
}
