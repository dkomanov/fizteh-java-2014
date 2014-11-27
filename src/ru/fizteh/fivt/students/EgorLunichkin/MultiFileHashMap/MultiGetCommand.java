package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class MultiGetCommand implements Command {
    public MultiGetCommand(MultiDataBase mdb, String key) {
        this.key = key;
        this.multiDataBase = mdb;
    }

    private MultiDataBase multiDataBase;
    private String key;

    public void run() throws MultiFileHashMapException {

    }
}
