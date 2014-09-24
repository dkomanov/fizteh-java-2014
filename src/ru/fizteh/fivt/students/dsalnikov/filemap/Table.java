package ru.fizteh.fivt.students.dsalnikov.filemap;

public interface Table {

    public String get(String key);

    public String put(String key, String value);

    public java.util.Set<String> list();

    public String remove(String key);

    public int exit();
}

