package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.io.File;

/**
 * @author AlexeyZhuravlev
 */
public class CreateCommand extends Command {

    private String tableName;

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    public CreateCommand(String passedName) {
        tableName = passedName;
    }

    public CreateCommand() {}

    protected final int numberOfArguments() {
        return 1;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (base.tables.containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            File newTable = new File(base.parentDirectory, tableName);
            if (!newTable.mkdir()) {
                throw new Exception("Unable to create working directory for new table");
            }
            base.tables.put(tableName, new MultiTable(newTable));
            System.out.println("created");
        }
    }
}
