package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

public class MultiDataBase {
    public MultiDataBase(String dbDir) throws Exception {
        if (dbDir == null) {
            throw new MultiFileHashMapException("No database directory name specified");
        }
        dbDirectory = new File(dbDir);
        using = null;
        tables = new HashMap<String, Table>();
        if (!Files.exists(dbDirectory.toPath()) && !dbDirectory.mkdir()) {
            throw new MultiFileHashMapException("Cannot create working directory");
        }
        if (!dbDirectory.isDirectory()) {
            throw new MultiFileHashMapException("Specified path is not a directory");
        }
        for (String tableName : dbDirectory.list()) {
            File tableDirectory = new File(dbDirectory, tableName);
            if (tableDirectory.isDirectory()) {
                tables.put(tableName, new Table(tableDirectory));
            } else {
                throw new MultiFileHashMapException(tableName + " from database is not a directory");
            }
        }
    }

    public HashMap<String, Table> tables;
    public File dbDirectory;
    public String using;

    public Table getUsing() {
        return tables.get(using);
    }
}
