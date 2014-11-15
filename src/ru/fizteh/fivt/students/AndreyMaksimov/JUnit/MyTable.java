package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.strings.Table;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class MyTable implements Table {

    private SingleTableDataBaseDir fakeDir;
    private HybridTable table;
    private String name;

    protected MyTable(HybridTable passedTable, String passedName) {
        table = passedTable;
        name = passedName;
        fakeDir = new SingleTableDataBaseDir(table);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        return table.dirtyTable.databases[dir][file].data.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String result = get(key);
        JUnitCommand putKey = new JUnitPut(key, value);
        PrintStream out = System.out;
        System.setOut(new PrintStream(new ImaginaryOutputStream()));
        try {
            putKey.execute(fakeDir);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        System.setOut(out);
        return result;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String result = get(key);
        JUnitCommand removeByKey = new JUnitRemove(key);
        PrintStream out = System.out;
        System.setOut(new PrintStream(new ImaginaryOutputStream()));
        try {
            removeByKey.execute(fakeDir);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        System.setOut(out);
        return result;
    }

    @Override
    public int size() {
        return table.dirtyTable.recordsNumber();
    }

    @Override
    public int commit() {
        PrintStream out = System.out;
        System.setOut(new PrintStream(new ImaginaryOutputStream()));
        try {
            return table.commit();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            System.setOut(out);
        }
    }

    @Override
    public int rollback() {
        return table.rollBack();
    }

    @Override
    public List<String> list() {
        List<String> result = new LinkedList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                result.addAll(table.dirtyTable.databases[i][j].data.keySet());
            }
        }
        return result;
    }
}


