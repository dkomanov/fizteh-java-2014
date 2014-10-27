package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;

import java.io.File;

public interface MultiTable extends Table {

    void create(String name);

    void drop(String name);

    void use(String name);

    void showTables();

    String getDbPath();

    File getDbFile();

    int getAmountOfChanges();
}
