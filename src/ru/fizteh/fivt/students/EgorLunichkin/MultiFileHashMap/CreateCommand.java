package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class CreateCommand implements Command {
    public CreateCommand(MultiDataBase mdb, String name) {
        this.tableName = name;
        this.multiDataBase = mdb;
    }

    private String tableName;
    private MultiDataBase multiDataBase;

    public void run() {

    }
}
