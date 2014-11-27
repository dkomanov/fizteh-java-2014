package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class MultiRemoveCommand implements Command {
    public MultiRemoveCommand(MultiDataBase mdb, String key) {
        this.key = key;
        this.multiDataBase = mdb;
    }

    private MultiDataBase multiDataBase;
    private String key;

    public void run() throws MultiFileHashMapException {

    }
}
