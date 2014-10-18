package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;

public interface MultiTable extends Table {

    void create(String name);

    void drop(String name);

    void use(String name);

    void showTables();
}
