package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public class DataBaseDir {

    public HashMap<String, MultiTable> tables;
    public String using;
    public File parentDirectory;

    public DataBaseDir(String path) throws Exception {
        parentDirectory = new File(path);
        using = null;
        tables = new HashMap<>();
        if (!Files.exists(parentDirectory.toPath()) && !parentDirectory.mkdir()) {
            throw new Exception("Cannot create working directory");
        }
        if (!parentDirectory.isDirectory()) {
            throw new Exception("Specified fizteh.db.dir is not a directory");
        }
        for (String childName: parentDirectory.list()) {
            File childDirectory = new File(parentDirectory, childName);
            if (childDirectory.isDirectory()) {
                tables.put(childName, new MultiTable(childDirectory));
            } else {
                throw new Exception(childName + " from databases directory is not a directory");
            }
        }
    }

    public MultiTable getUsing() {
        return tables.get(using);
    }

}
