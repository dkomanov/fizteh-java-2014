package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class GetCommand extends CommandFileMap {

    private String key;

    public int numberOfArguments() {
        return 1;
    }

    public void putArguments(String[] args) {
        key = args[1];
    }

    public GetCommand(String passedKey) {
        key = passedKey;
    }

    public GetCommand() {
    }

    @Override
    public void execute(DataBase base) {
        String value = base.data.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
