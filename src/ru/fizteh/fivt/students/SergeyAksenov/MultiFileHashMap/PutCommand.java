package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

public class PutCommand implements Command {
    public void run(String[] args, DataBase dataBase)
            throws MultiFileMapException {
        if (!Executor.checkArgNumber(3, args.length, 3)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        if (dataBase.getCurrentTablePath() == null) {
            System.out.println("no table");
            return;
        }
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
            dataBase.getDataBase().put(key, value);
        }
    }
}