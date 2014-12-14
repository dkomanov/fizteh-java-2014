package ru.fizteh.fivt.students.ryad0m.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.*;

public class UserTable implements Table {
    MyTable myTable;
    HashMap<String, String> operations = new HashMap<>();
    HashSet<String> deleted = new HashSet<>();

    public UserTable(MyTable myTable) {
        this.myTable = myTable;
    }

    @Override
    public String getName() {
        return myTable.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (deleted.contains(key)) {
            return null;
        } else if (operations.containsKey(key)) {
            return operations.get(key);
        } else {
            return myTable.get(key);
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String res = get(key);
        deleted.remove(key);
        operations.put(key, value);
        return res;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String res = get(key);
        operations.remove(key);
        deleted.add(key);
        return res;
    }

    @Override
    public int size() {
        Set<String> set = myTable.getKeys();
        set.addAll(operations.keySet());
        set.removeAll(deleted);
        return set.size();
    }

    @Override
    public int commit() {
        int res = operations.size() + deleted.size();
        for (Map.Entry<String, String> operation : operations.entrySet()) {
            myTable.put(operation.getKey(), operation.getValue());
        }
        for (String str : deleted) {
            myTable.remove(str);
        }
        operations.clear();
        deleted.clear();
        myTable.save();
        return res;
    }

    public int unsavedSize() {
        return operations.size() + deleted.size();
    }

    @Override
    public int rollback() {
        int res = operations.size() + deleted.size();
        operations.clear();
        deleted.clear();
        return res;
    }

    @Override
    public List<String> list() {
        Set<String> set = myTable.getKeys();
        set.addAll(operations.keySet());
        set.removeAll(deleted);
        return new ArrayList<String>(set);
    }

}
