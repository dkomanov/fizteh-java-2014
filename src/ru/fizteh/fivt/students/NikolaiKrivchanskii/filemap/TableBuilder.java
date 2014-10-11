package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import java.io.File;
import java.util.Set;

public interface TableBuilder {
    String get(String key);

    void put(String key, String value);

    Set<String> getKeys();

    File getTableDirectory();

    void setCurrentFile(File currentFile);
}
