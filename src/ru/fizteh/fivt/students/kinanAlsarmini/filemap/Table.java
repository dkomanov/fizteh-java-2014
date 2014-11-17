package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Table {
    private HashMap<String, String> hMap;

    public Table() {
        hMap = new HashMap<String, String>();
    }

    public Table(Table other) {
        hMap = (HashMap<String, String>) other.hMap.clone();
    }

    public boolean exists(String k) {
        return hMap.containsKey(k);
    }

    public void put(String k, String v) {
        hMap.put(k, v);
    }

    public void clear() {
        hMap.clear();
    }

    public String get(String k) {
        return hMap.get(k);
    }

    public void remove(String k) {
        hMap.remove(k);
    }

    public Set<Map.Entry<String, String>> listRows() {
        return hMap.entrySet();
    }

    public Set<String> keySet() {
        return hMap.keySet();
    }
};
