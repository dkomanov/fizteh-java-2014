package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

public class ExitCommand implements Command {
    public void run(final String[] args, DataBase dataBase)
            throws MultiFileMapException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            dataBase.close();
            System.out.println("Invalid number of arguments");
            System.exit(-1);
        }
        dataBase.close();
        System.exit(0);
    }
}