package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public class DropCommand extends Command {
    private final String tableName;

    public DropCommand(String passedTableName) {
        tableName = passedTableName;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exist");
        } else {
            base.tables.get(tableName).drop();
            base.tables.remove(tableName);
            System.out.println("dropped");
        }
    }
}
