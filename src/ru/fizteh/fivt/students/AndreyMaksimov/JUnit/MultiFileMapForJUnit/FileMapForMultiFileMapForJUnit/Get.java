package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;


public class Get extends Command {

    private String key;

    public int numberOfArguments() {
        return 1;
    }

    public void putArguments(String[] args) {
        key = args[1];
    }

    public Get(String passedKey) {
        key = passedKey;
    }

    public Get() {
    }

    @Override
    public void startNeedInstruction(DataBase base) {
        String value = base.data.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}