package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.File;

public class CreateCommand extends Command {

    private String tableName;

    public CreateCommand(String newTableName) {
        tableName = newTableName;
    }

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        if (base.tables.containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            File newTable = new File(base.mainDirectory, tableName);
            if (!newTable.mkdir()) {
                throw new MkdirException();
            }
            base.tables.put(tableName, new Table(newTable));
            System.out.println("created");
        }
    }
}
