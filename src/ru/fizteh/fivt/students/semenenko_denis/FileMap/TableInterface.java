package ru.fizteh.fivt.students.semenenko_denis.FileMap;

import java.util.List;

/**
 * Created by denny_000 on 08.10.2014.
 */
public interface TableInterface {
    String put(String key, String value);

    String get(String key);

    boolean remove(String key);

    List<String> list();
}
