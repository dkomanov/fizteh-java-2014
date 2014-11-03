package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.DataBase;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTable {
    private static final int SIZE1 = 16;
    private static final int SIZE2 = 16;
    public String tableName;
    public Path path;
    public DataBase[][] tableDateBase;
    public BaseTable(String name) {
        tableName = name;
        path = Paths.get(name);
        tableDateBase = new DataBase[SIZE1][SIZE2];
    }
}

