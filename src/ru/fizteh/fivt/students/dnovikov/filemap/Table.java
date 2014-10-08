package ru.fizteh.fivt.students.dnovikov.filemap;

public interface Table {
    String put(String key, String value);

    String get(String key);

    java.util.Set<String> list();

    String remove(String key);
}
