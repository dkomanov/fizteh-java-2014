package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import java.io.File;

public class Create extends Command {

    private final String tableName;

    public Create(String nameToCreate) {
        tableName = nameToCreate;
    }

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (base.tables.containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            File newTable = new File(base.parentDirectory, tableName);
            if (!newTable.mkdir()) {
                throw new Exception("Unable to create directory for new table");
            }
            base.tables.put(tableName, new Table(newTable));
            System.out.println("created");
        }
    }
}
