package ru.fizteh.fivt.students.dsalnikov.filemap;

public interface Table {

    String get(String key);

    String put(String key, String value);

    void list();

    String remove(String key);

    int exit();
}

