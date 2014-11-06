package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.File;
import java.util.HashMap;

public class DataBaseOneDir {

    HashMap<String, Table> tables;
    String using;
    File mainDirectory;

    public DataBaseOneDir(String path) throws Exception {
        mainDirectory = new File(path);
        tables = new HashMap<>();
        if (!mainDirectory.exists()) {
            throw new ExistsException();
        }
        if (!mainDirectory.isDirectory()) {
            throw new Exception("Database isn't a directory");
        }
        for (String childName: mainDirectory.list()) {
            File childDirectory = new File(mainDirectory, childName);
            if (childDirectory.isHidden()) {
                continue;
            }
            if (childDirectory.isDirectory()) {
                tables.put(childName, new Table(childDirectory));
            } else {
                throw new Exception(childName + " doesn't a directory, but inside a main directory");
            }
        }
    }

    public Table getUsing() {
        return tables.get(using);
    }
}
