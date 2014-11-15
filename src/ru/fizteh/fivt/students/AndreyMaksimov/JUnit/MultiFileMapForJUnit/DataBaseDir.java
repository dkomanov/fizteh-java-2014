package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;


public class DataBaseDir {
    public HashMap<String, Table> tables;
    public String using;
    File parentDirectory;

    public DataBaseDir(String needBasePath) throws Exception {
        parentDirectory = new File(needBasePath);
        using = null;
        tables = new HashMap<>();
        if (!Files.exists(parentDirectory.toPath()) && !parentDirectory.mkdir()) {
            throw new Exception("Cannot create directory");
        }
        if (!parentDirectory.isDirectory()) {
            throw new Exception("Unfortunately fizteh.db.dir is not a directory");
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

