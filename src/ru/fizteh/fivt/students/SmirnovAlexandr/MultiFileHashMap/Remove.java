package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

public class Remove extends CommandFileMap {

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
