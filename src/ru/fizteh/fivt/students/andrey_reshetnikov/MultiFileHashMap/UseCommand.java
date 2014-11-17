package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class UseCommand extends Command {

    private String tableName;

    public UseCommand(String newTableName) {
        tableName = newTableName;
    }

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exists");
        } else {
            base.using = tableName;
            System.out.println("using " + tableName);
        }
    }
}
