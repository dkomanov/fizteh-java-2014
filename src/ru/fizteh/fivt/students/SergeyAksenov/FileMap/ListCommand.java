package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

import java.util.Set;

//
public class ListCommand implements Command {
    public void run(String[] args, DataBase dataBase, Environment env) throws FileMapException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            ErrorHandler.countArguments("list");
        }
        Set<String> keySet = dataBase.getDataBase().keySet();
        System.out.println(String.join(", ", keySet));
    }
}
