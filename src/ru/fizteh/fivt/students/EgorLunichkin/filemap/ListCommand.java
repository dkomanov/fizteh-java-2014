package ru.fizteh.fivt.students.EgorLunichkin.filemap;

import java.util.Iterator;
import java.util.Set;

public class ListCommand implements Command {
    public ListCommand(DataBase _db) {
        this.dataBase = _db;
    }

    private DataBase dataBase;

    public void run() throws FileMapException {
        Set<String> keys = dataBase.getDataBase().keySet();
        Iterator<String> it = keys.iterator();
        if (it.hasNext()) {
            System.out.print(it.next());
        }
        while (it.hasNext()) {
            System.out.print(", " + it.next());
        }
        System.out.println();
    }
}
