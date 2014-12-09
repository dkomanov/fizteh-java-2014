package ru.fizteh.fivt.students.egor_belikov.Parallel;

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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTableProvider implements TableProvider {

    public static String currentPath;
    public static Map<String, Table> listOfTables;
    public static MyTable currentTable;
    private MySerializer ts = new MySerializer();
    private final ReadWriteLock tableAccessLock = new ReentrantReadWriteLock(true);


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
        tableAccessLock.readLock().lock();
        try {
            return ParallelMain.myTableProvider.listOfTables.get(name);
        } finally {
            tableAccessLock.readLock().unlock();
        }
    }


    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("Directory name or types of columns is null");
        }
        tableAccessLock.writeLock().lock();
        try {
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
        } finally {
            tableAccessLock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Name table is null");
        }
        tableAccessLock.writeLock().lock();
        try {
            String pathToTable = ParallelMain.myTableProvider.currentPath + File.separator + name;
            File table = new File(pathToTable);
            if (!table.exists() || !table.isDirectory()) {
                throw new IllegalStateException("Table not exist");
            } else {
                if (ParallelMain.myTableProvider.currentTable != null) {
                    if (ParallelMain.myTableProvider.currentTable.getName().equals(name)) {
                        ParallelMain.myTableProvider.currentTable = null;
                    }
                }
                MyTable t = (MyTable) ParallelMain.myTableProvider.listOfTables.get(name);
                ParallelMain.myTableProvider.listOfTables.remove(name);
                t.rm();
                table.delete();
            }
        } finally {
            tableAccessLock.writeLock().unlock();
        }
    }
    public Storeable deserialize(Table table, String value) throws ParseException {
        return ts.deserialize(table, value, ((MyTable) table).signature);
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
