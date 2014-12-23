package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem;

import java.io.File;
import java.util.Set;

public interface DatabaseTableCreatorInterface {
    String get(String key);

    void put(String key, String value);

    Set<String> getKeys();

    File getTableDirectory();

    void setCurrentFile(File currentFile);
}
