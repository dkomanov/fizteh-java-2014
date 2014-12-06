package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class DropCommand extends Command {
    private String tableName;

    protected void putArguments(String[] args) {
        tableName = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    public DropCommand(String passedName) {
        tableName = passedName;
    }

    public DropCommand() {}

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
