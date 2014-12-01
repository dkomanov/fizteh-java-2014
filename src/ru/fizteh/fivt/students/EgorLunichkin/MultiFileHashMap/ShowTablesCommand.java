package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import java.util.HashMap;

public class ShowTablesCommand implements Command {
    public ShowTablesCommand(MultiDataBase mdb) {
        this.multiDataBase = mdb;
    }

    private MultiDataBase multiDataBase;

    public void run() throws MultiFileHashMapException {
        for (HashMap.Entry<String, Table> entry : multiDataBase.tables.entrySet()) {
            String name = entry.getKey();
            String rowCount = String.valueOf(entry.getValue().tableSize());
            System.out.println(name + " " + rowCount);
        }
    }

    public void runOnTable(Table table) {}
}
