package ru.fizteh.fivt.students.dnovikov.filemap;

import java.io.IOException;
import java.util.Set;

public interface Table {
    String put(String key, String value);

    String get(String key);

    Set<String> list();

    String remove(String key);

    void load() throws IOException;

    void save() throws IOException;

}
