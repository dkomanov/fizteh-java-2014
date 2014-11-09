package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.DataBase;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class BaseTable {
    int SIZEDIR = 16;
    int SIZEDAT = 16;
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

