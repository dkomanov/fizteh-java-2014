package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class RemoveCommand extends CommandFileMap {

    private String key;

    public void putArguments(String[] args) {
        key = args[1];
    }

    public int numberOfArguments() {
        return 1;
    }

    public RemoveCommand(String passedKey) {
        key = passedKey;
    }

    public RemoveCommand() {
    }

    @Override
    public void execute(DataBase base) throws Exception {
        if (base.data.containsKey(key)) {
            base.data.remove(key);
            base.dump();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
