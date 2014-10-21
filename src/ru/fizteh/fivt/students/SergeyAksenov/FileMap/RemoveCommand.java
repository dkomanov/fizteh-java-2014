package ru.fizteh.fivt.students.SergeyAksenov.FileMap;


public class RemoveCommand implements Command {
    public void run(String[] args, FileDataBase dataBase, Environment env)
            throws FileMapException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            ErrorHandler.countArguments("remove");
        }//
        if (!args[1].isEmpty()) {
            String key = args[1];
            if (dataBase.getDataBase().containsKey(key)) {
                dataBase.getDataBase().remove(key);
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }
}
