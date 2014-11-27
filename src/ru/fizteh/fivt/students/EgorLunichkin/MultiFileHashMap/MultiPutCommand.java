package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class MultiPutCommand implements Command {
    public MultiPutCommand(MultiDataBase mdb, String key, String value) {
        this.key = key;
        this.value = value;
        this.multiDataBase = mdb;
    }

    private String key;
    private String value;
    private MultiDataBase multiDataBase;

    public void run() throws MultiFileHashMapException {

    }
}
