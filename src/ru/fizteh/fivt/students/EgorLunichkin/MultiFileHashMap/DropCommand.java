package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class DropCommand implements Command {
    public DropCommand(MultiDataBase mdb, String name) {
        this.tableName = name;
        this.multiDataBase = mdb;
    }

    private String tableName;
    private MultiDataBase multiDataBase;

    public void run() throws MultiFileHashMapException {
        if (!multiDataBase.tables.containsKey(tableName)) {
            System.out.println(tableName + " not exists");
        } else {
            multiDataBase.tables.get(tableName).drop();
            multiDataBase.tables.remove(tableName);
            System.out.println("dropped");
        }
    }
}