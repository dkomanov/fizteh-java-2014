package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class DropCommand extends Command {
    private String tableName;

    public DropCommand(String newTableName) {
        tableName = newTableName;
    }

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        if (!base.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exist");
        } else {
            base.tables.get(tableName).drop();
            base.tables.remove(tableName);
            System.out.println("dropped");
        }
    }
}
