package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

import java.util.Set;

public class ListCommand implements Command {
    public void run(String[] args, DataBase dataBase)
            throws MultiFileMapException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        if (dataBase.getCurrentTablePath() == null) {
            System.out.println("no table");
            return;
        }
        Set<String> keySet = dataBase.getDataBase().keySet();
        for (String key : keySet) {
            System.out.print(key + ",");
        }
        System.out.println();
    }
}
