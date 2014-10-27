package ru.fizteh.fivt.students.dsalnikov.filemap;

import java.util.List;

public interface Table extends ru.fizteh.fivt.storage.strings.Table {

    List<String> list();

    String remove(String key);

    int exit();
}
