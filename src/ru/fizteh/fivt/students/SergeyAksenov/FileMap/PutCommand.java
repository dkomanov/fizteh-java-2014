package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

public class PutCommand implements Command {
    public void run(String[] args, DataBase dataBase, Environment env)
            throws FileMapException {
        if (!Executor.checkArgNumber(3, args.length, 3)) {
            ErrorHandler.countArguments("put");
        }//
        if (!args[1].isEmpty() && !args[2].isEmpty()) {
            String key = args[1];
            String value = args[2];
            if (!DataBase.getDataBase().containsKey(key)) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(DataBase.getDataBase().get(key));
                DataBase.getDataBase().remove(key);
            }
            dataBase.put(key, value);
        }
    }
}
