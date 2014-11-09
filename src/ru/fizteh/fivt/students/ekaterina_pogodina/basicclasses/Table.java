package ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses;

import java.io.FileNotFoundException;
import java.util.List;

public interface Table {

    String getName();

    String get(String key);

    String put(String key, String value) throws Exception;

    String remove(String key);

    int size() throws Exception;

    int commit() throws Exception;

    int rollback() throws Exception;

    List<String> list();
}