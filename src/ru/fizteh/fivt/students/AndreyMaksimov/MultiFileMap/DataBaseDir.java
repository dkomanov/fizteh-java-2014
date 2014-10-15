package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import java.io.File;
import java.util.HashMap;
import java.nio.file.Files;


public class DataBaseDir {
    HashMap<String, Table> tables;
    String using;
    File parentDirectory;

    public DataBaseDir(String needPath) throws Exception {
        parentDirectory = new File(needPath);
        using = null;
        tables = new HashMap<>();
        if (!Files.exists(parentDirectory.toPath())) {
            throw new Exception("ERROR: Databases directory does not exists");
        }
        if (!parentDirectory.isDirectory()) {
            throw new Exception("ERROR: Unfortunately fizteh.db.dir is not a directory");
        }
        for (String childName : parentDirectory.list()) {
            File childDirectory = new File(parentDirectory, childName);
            if (childDirectory.isDirectory()) {
                tables.put(childName, new Table(childDirectory));
            } else {
                throw new Exception(childName + " from database directory is not a directory");
            }
        }
    }

    public Table getUsing() {
        return tables.get(using);
    }
}

