package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.io.File;

/**
 * @author AlexeyZhuravlev
 */
public class CreateCommand extends Command {

    private final String tableName;

    public CreateCommand(String passedTableName) {
        tableName = passedTableName;
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
            base.tables.put(tableName, new Table(newTable));
            System.out.println("created");
        }
    }
}
