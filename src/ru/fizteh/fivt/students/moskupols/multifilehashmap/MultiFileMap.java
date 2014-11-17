package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.IOException;
import java.util.List;

/**
 * Created by moskupols on 06.11.14.
 */
public interface MultiFileMap {
    String getName();

    String get(String key) throws IOException;

    String put(String key, String value) throws IOException;

    String remove(String key) throws IOException;

    int size();

    List<String> list() throws IOException;

    void clear() throws IOException;

    void flush() throws IOException;
}
