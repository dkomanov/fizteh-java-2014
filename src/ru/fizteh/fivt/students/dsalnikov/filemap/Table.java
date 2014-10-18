package ru.fizteh.fivt.students.dsalnikov.filemap;

public interface Table extends ru.fizteh.fivt.storage.strings.Table {

    void list();

    String remove(String key);

    int exit();
}
