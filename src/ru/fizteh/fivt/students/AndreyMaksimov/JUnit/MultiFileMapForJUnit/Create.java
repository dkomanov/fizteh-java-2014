package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import java.io.File;

public class Create extends Command {

    private String tableName;

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    public Create(String passedName) {
        tableName = passedName;
    }

    public Create() {}

    protected final int numberOfArguments() {
        return 1;
    }

    @Override
    public void startNeedInstruction(DataBaseDir base) throws Exception {
        if (base.tables.containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            File newTable = new File(base.parentDirectory, tableName);
            if (!newTable.mkdir()) {
                throw new Exception("Unable to create directory for table");
            }
            base.tables.put(tableName, new Table(newTable));
            System.out.println("created");
        }
    }
}