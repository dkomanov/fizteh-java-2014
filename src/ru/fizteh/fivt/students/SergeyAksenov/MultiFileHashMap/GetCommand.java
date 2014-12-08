package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

public class GetCommand implements Command {
    public void run(String[] args, DataBase dataBase)
            throws MultiFileMapException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        if (dataBase.getCurrentTablePath() == null) {
            System.out.println("no table");
            return;
        }
        if (!args[1].isEmpty()) {
            String key = args[1];
            if (!dataBase.getDataBase().containsKey(key)) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(dataBase.getDataBase().get(key));
            }
        }
    }
}
