package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;

import java.io.File;
import java.util.List;

public interface MultiTable extends Table {

    void create(List<String> name);

    void drop(String name);

    void use(String name);

    List<String> showTables();

    String getDbPath();

    File getDbFile();

    int getAmountOfChanges();

}
