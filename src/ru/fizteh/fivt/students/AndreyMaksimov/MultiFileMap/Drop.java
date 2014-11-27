package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

public class Drop extends Command {
    private final String tableName;

    public Drop(String passedTableName) {
        tableName = passedTableName;
    }

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exist");
        } else {
            base.tables.get(tableName).drop();
            base.tables.remove(tableName);
            System.out.println("dropped");
        }
    }
}

