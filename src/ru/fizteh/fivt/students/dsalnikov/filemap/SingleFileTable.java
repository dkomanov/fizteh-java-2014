package ru.fizteh.fivt.students.dsalnikov.filemap;


import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SingleFileTable extends ShellState implements Table {

    private Map<String, String> storage;

    //read from file to map
    public SingleFileTable() {
        String dbfile = System.getProperty("db.file");
        storage = new HashMap<>();
    }

    @Override
    public String get(String key) {
        return storage.get(key);
    }

    @Override
    public String put(String key, String value) {
        return storage.put(key, value);
    }

    @Override
    public Set<String> list() {
        return storage.keySet();
    }

    @Override
    public String remove(String key) {
        return storage.remove(key);
    }

    @Override
    public int exit() {
        return 0;
    }
}
