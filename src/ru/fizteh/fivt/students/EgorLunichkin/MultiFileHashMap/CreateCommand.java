package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import java.io.File;

public class CreateCommand implements Command {
    public CreateCommand(MultiDataBase mdb, String name) {
        this.tableName = name;
        this.multiDataBase = mdb;
    }

    private String tableName;
    private MultiDataBase multiDataBase;

    public void run() throws Exception {
        if (multiDataBase.tables.containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            File tableDirectory = new File(multiDataBase.dbDirectory, tableName);
            if (!tableDirectory.mkdir()) {
                throw new MultiFileHashMapException("Unable to create directory for new table");
            }
            multiDataBase.tables.put(tableName, new MultiTable(tableDirectory));
            System.out.println("created");
        }
    }

    public void runOnTable(MultiTable table) {}
}
