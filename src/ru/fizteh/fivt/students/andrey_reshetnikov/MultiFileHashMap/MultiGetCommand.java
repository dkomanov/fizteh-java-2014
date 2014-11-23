package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class MultiGetCommand extends Command {
    private String key;

    protected void putArguments(String[] args) {
        key = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    public MultiGetCommand(String passedKey) {
        key = passedKey;
    }

    public MultiGetCommand() {}

    @Override
    public void executeOnTable(Table table) throws Exception {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % ConstClass.NUM_DIRECTORIES;
        int file = hashCode / ConstClass.NUM_FILES % ConstClass.NUM_FILES;
        GetCommand get = new GetCommand(key);
        DataBase db = table.databases[dir][file];
        if (db == null) {
            System.out.println("not found");
        } else {
            get.execute(table.databases[dir][file]);
        }
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            executeOnTable(base.getUsing());
        }
    }
}
