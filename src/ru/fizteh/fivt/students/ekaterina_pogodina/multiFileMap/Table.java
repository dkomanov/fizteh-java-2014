package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap;

import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.DataBase;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Table {
    public String tableName;
    public Path path;
    public DataBase[][] tableDateBase;
    public Table(String name) {
        tableName = name;
        path = Paths.get(name);
        tableDateBase = new DataBase[16][16];
    }
}
