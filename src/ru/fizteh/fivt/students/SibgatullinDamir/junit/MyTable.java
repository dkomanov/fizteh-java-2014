package ru.fizteh.fivt.students.SibgatullinDamir.junit;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lenovo on 09.11.2014.
 */
public class MyTable implements Table {

    private FileMap currentTable;
    private FileMap committedTable;
    int changedKeys;

    protected MyTable(FileMap passedTable) {
        currentTable = passedTable;
        committedTable = new FileMap();
        committedTable.putAll(passedTable);
        changedKeys = 0;
    }

    public String getName() {
        return currentTable.name;
    }

    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return currentTable.get(key);
    }

    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String result = get(key);
        PutCommand putKey = new PutCommand();
        String[] args = {"put", key, value};
        try {
            putKey.execute(args, currentTable);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        ++changedKeys;
        return result;
    }

    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String result = get(key);
        RemoveCommand removeByKey = new RemoveCommand();
        String[] args = {"remove", key};
        try {
            removeByKey.execute(args, currentTable);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        ++changedKeys;
        return result;
    }

    public int size() {
        return currentTable.size();
    }

    public int commit() {
        try {
            currentTable.write();
        } catch (MyException e) {
            throw new RuntimeException();
        }
        committedTable.clear();
        committedTable.putAll(currentTable);
        int countOfChanges = changedKeys;
        changedKeys = 0;
        return countOfChanges;
    }

    public int rollback() {
        currentTable.clear();
        currentTable.putAll(committedTable);
        int countOfChanges = changedKeys;
        changedKeys = 0;
        return countOfChanges;
    }

    public List<String> list() {
        List<String> result = new LinkedList<>();
        for (String key: currentTable.keySet()) {
            result.add(key);
        }
        return result;
    }
}
