package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

import java.io.File;

public class UseCommand implements Command {
    public void run(String[] args, DataBase dataBase) {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
        }
        File tableDirectory = new File(dataBase.getDataBasePath() + File.separator + args[1]);
        if (!tableDirectory.exists()) {
            System.out.println(args[1] + " not exists");
        }
        dataBase.setUsingTable(args[1]);
    }
}