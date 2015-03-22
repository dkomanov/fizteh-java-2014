package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class BaseTable {

    static final int SIZEDIR = 16;
    static final int SIZEDAT = 16;
    public String tableName;
    public Path path;
    public DataBase[][] tableDateBase;
    public Map<String, String> keys;
    public Map<String, String> puted;
    public Set<String> removed;
    public BaseTable(String name, Path pathTable) {
        keys = new HashMap<>();
        puted = new HashMap<>();
        removed = new HashSet<>();
        tableName = name;
        path = pathTable.resolve(Paths.get(name));
        tableDateBase = new DataBase[SIZEDIR][SIZEDAT];
    }
    public  BaseTable() {
        keys = new HashMap<>();
        puted = new HashMap<>();
        removed = new HashSet<>();
        tableDateBase = new DataBase[SIZEDIR][SIZEDAT];
    }
}
