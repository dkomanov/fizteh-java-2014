package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

import java.util.Set;
//
public class ListCommand implements Command {
    public void run(String[] args, DataBase dataBase)
            throws MultiFileMapException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
        }
        Set <String> keySet = dataBase.getDataBase().keySet();
        for (String key : keySet) {
            System.out.print(key + ",");
        }
        System.out.println();
        }
    }
