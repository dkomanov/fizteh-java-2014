package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

public class DropCommand implements Command{
    public void run (String[] args, DataBase dataBase)
            throws MultiFileMapException{
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
        }
        dataBase.drop(args[1]);
    }
}
