package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;
import ru.fizteh.fivt.students.EgorLunichkin.filemap.ListCommand;

public class MultiListCommand implements Command {
    public MultiListCommand(MultiDataBase mdb) {
        this.multiDataBase = mdb;
    }

    public MultiListCommand() {
        this.multiDataBase = null;
    }

    private MultiDataBase multiDataBase;

    public void run() {
        if (multiDataBase.using == null) {
            System.out.println("no table");
        } else {
            this.runOnTable(multiDataBase.getUsing());
        }
    }

    public void runOnTable(Table table) {
        StringBuilder listKeys = new StringBuilder();
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                DataBase dataBase = table.dataBases[dir][file];
                if (dataBase != null) {
                    String curList = new ListCommand(dataBase).list();
                    if (curList.length() > 0) {
                        if (listKeys.length() > 0) {
                            listKeys.append(", ");
                        }
                        listKeys.append(curList);
                    }
                }
            }
        }
        System.out.println(listKeys.toString());
    }
}
