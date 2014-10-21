package ru.fizteh.fivt.students.SergeyAksenov.FileMap;


public class GetCommand implements Command {
    public void run(String[] args, DataBase dataBase, Environment env)
            throws FileMapException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            ErrorHandler.countArguments("get");
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
