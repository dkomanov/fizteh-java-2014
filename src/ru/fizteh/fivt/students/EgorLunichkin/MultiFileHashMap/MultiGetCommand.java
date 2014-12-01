package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;
import ru.fizteh.fivt.students.EgorLunichkin.filemap.GetCommand;

public class MultiGetCommand implements Command {
    public MultiGetCommand(MultiDataBase mdb, String key) {
        this.key = key;
        this.multiDataBase = mdb;
    }

    public MultiGetCommand(String key) {
        this.key = key;
        this.multiDataBase = null;
    }

    private MultiDataBase multiDataBase;
    private String key;

    public void run() {
        if (multiDataBase.using == null) {
            System.out.println("no table");
        } else {
            this.runOnTable(multiDataBase.getUsing());
        }
    }

    public void runOnTable(Table table) {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        DataBase dataBase = table.dataBases[dir][file];
        if (dataBase == null) {
            System.out.println("not found");
        } else {
            new GetCommand(dataBase, key).run();
        }
    }
}
