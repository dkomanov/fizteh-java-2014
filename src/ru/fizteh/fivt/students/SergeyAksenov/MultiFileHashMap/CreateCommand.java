package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

import java.io.File;

public class CreateCommand implements Command {
    public void run(final String[] args, DataBase dataBase) {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        File table = new File(dataBase.getDataBasePath() + File.separator + args[1]);
        if (table.exists()) {
            System.out.println(args[1] + " exists");
            return;
        }
        if (!table.mkdir()) {
            System.out.println("Cannot create directory for new table");
            return;
        }
        System.out.println("created");
    }
}