package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

public class Drop extends Command {
    private String tableName;

    protected void putArguments(String[] args) {
        tableName = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    public Drop(String passedName) {
        tableName = passedName;
    }

    public Drop() {}

    @Override
    public void startNeedInstruction(DataBaseDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exist");
        } else {
            base.tables.get(tableName).drop();
            base.tables.remove(tableName);
            System.out.println("dropped");
        }
    }
}
