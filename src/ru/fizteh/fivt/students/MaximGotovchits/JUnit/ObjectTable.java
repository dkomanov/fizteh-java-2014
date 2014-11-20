package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.*;

public class ObjectTable extends CommandTools implements Table {
    static int overwriteNum = 0;
    String tableName;
    public ObjectTable() {
        tableName = new String();
    }
    public ObjectTable(String name) {
        this.tableName = name;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(this.tableName);
    }
    @Override
    public boolean equals(Object obj) { // Возможно тут нужно было как-то использовать hashCode,
    // но я не знаю как, особенно если учесть, что он возваращает Objects, а не Object.
        ObjectTable tableObj = (ObjectTable) obj;
        if (this.tableName.equals(tableObj.tableName)) {
            return true;
        }
        return false;
    }
    @Override
    public String getName() {
        return tableName;
    }
    @Override
    public String get(String key) throws IllegalArgumentException {
        try {
            if (key == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        String val = storage.get(key);
        if (val != null) {
            System.out.println("found");
            System.out.println(val);
        } else {
            System.out.println("not found");
        }
        return val; // If found returns key, else returns null (http://bit.ly/1EcAn27).
    }
    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        try {
            if (key == null || value == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        String previousValue = storage.put(key, value);
        if (previousValue == null) {
            lastChanges.push("remove" + " " + key);
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            lastChanges.push("put" + " " + key + " " + previousValue);
            ++overwriteNum;
            System.out.println(previousValue);
        }
        return previousValue;
    }
    @Override
    public String remove(String key) throws IllegalArgumentException {
        try {
            if (key == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        String val = storage.remove(key);
        if (val != null) {
            lastChanges.push("put" + " " + key + " " + val);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return val;
    }
    @Override
    public int size() {
        return storage.size();
    }
    @Override
    public int commit() {
        int savedKeys = Math.abs(storage.size() - commitStorage.size());
        Map<String, String> tmp = new HashMap<String, String>(commitStorage);
        commitStorage = new HashMap<String, String>(storage);
        lastChanges.clear();
        return savedKeys;
    }
    @Override
    public int rollback() throws IllegalArgumentException {
        int changes = Math.abs(storage.size() - commitStorage.size() + overwriteNum);
        while (!lastChanges.isEmpty()) {
            String[] tmpCmd = lastChanges.pop().toString().split(" ");
            if (tmpCmd[0].equals("put")) {
                storage.put(tmpCmd[1], tmpCmd[2]);
            }
            if (tmpCmd[0].equals("remove")) {
                storage.remove(tmpCmd[1]);
            }
        }
        overwriteNum = 0;
        return changes;
    }
    @Override
    public LinkedList<String> list() {
        LinkedList<String> list = new LinkedList<String>();
        int size = 0;
        Set<String> k = storage.keySet();
        for (Object iter : k) {
            list.add(iter.toString());
            if (size < storage.size() - 1) {
                System.out.print(iter + ", ");
            } else {
                System.out.print(iter);
            }
            ++size;
        }
        if (size != 0) {
            System.out.println();
        }
        return list;
    }
}
