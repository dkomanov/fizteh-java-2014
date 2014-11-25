package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

public class Use extends Command {

    private final String tableName;

    public Use(String passedTableName) {
        tableName = passedTableName;
    }

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exists");
        } else {
            base.using = tableName;
            System.out.println("using " + tableName);
        }
    }
}
