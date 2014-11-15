package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

public class Remove extends Command {

    private String key;

    public void putArguments(String[] args) {
        key = args[1];
    }

    public int numberOfArguments() {
        return 1;
    }

    public Remove(String passedKey) {
        key = passedKey;
    }

    public Remove() {
    }

    @Override
    public void startNeedInstruction(DataBase base) throws Exception {
        if (base.data.containsKey(key)) {
            base.data.remove(key);
            base.sync();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}