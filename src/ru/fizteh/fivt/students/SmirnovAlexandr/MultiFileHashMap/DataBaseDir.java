package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

import java.io.File;
import java.util.HashMap;

public class DataBaseDir {

    public HashMap<String, Table> tables;
    public String using;
    public File parentDirectory;

    public DataBaseDir(String path) throws Exception {
        parentDirectory = new File(path);
        tables = new HashMap<>();
        if  (!(parentDirectory.exists()) && !parentDirectory.mkdir()) {
            throw new Exception("Can not create working directory");
        }
        if (!parentDirectory.isDirectory()) {
            throw new Exception("Specified fizteh.db.dir is not a directory");
        }
        for (String childName: parentDirectory.list()) {
            File childDirectory = new File(parentDirectory, childName);
            if (childDirectory.isHidden()) {
                continue;
            }
            if (childDirectory.isDirectory()) {
                tables.put(childName, new Table(childDirectory));
            } else {
                throw new Exception(childName + " from databases directory is not a directory");
            }
        }
    }

    public Table getUsing() {
        return tables.get(using);
    }

}
