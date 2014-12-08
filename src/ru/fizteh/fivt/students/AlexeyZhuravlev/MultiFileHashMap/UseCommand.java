package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public class UseCommand extends Command {

    private String tableName;

    protected void putArguments(String[] args) {
        tableName = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    public UseCommand() {}

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
