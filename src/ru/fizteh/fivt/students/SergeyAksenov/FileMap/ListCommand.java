package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

import java.util.Iterator;
import java.util.Set;
//
public class ListCommand implements Command {
    public void run(String[] args, DataBase dataBase, Environment env) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            //
        }
        Set keySet = DataBase.getDataBase().keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            System.out.println(it + ", ");
        }
        System.out.println();
    }
}
