package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public class UseCommand extends Command {

    private final String tableName;

    public UseCommand(String passedTableName) {
        tableName = passedTableName;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exists");
        } else {
            base.using = tableName;
            System.out.println("using " + tableName);
        }
    }
}
