package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

public class Use extends Command {

    private String tableName;

    protected void putArguments(String[] args) {
        tableName = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    public Use() {}

    public Use(String passedTableName) {
        tableName = passedTableName;
    }

    @Override
    public void startNeedInstruction(DataBaseDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exists");
        } else {
            base.using = tableName;
            System.out.println("using " + tableName);
        }
    }
}
