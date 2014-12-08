package ru.fizteh.fivt.students.EgorLunichkin.filemap;

import java.util.Iterator;
import java.util.Set;

public class ListCommand implements Command {
    public ListCommand(DataBase db) {
        this.dataBase = db;
    }

    private DataBase dataBase;

    public String list() {
        StringBuilder listKeys = new StringBuilder();
        Set<String> keys = dataBase.getDataBase().keySet();
        Iterator<String> it = keys.iterator();
        if (it.hasNext()) {
            listKeys.append(it.next());
        }
        while (it.hasNext()) {
            listKeys.append(", " + it.next());
        }
        return listKeys.toString();
    }

    public void run() throws FileMapException {
        System.out.println(list());
    }
}
