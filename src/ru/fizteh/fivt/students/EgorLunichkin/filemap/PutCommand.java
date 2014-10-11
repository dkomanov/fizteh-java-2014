package ru.fizteh.fivt.students.EgorLunichkin.filemap;

import java.util.HashMap;

public class PutCommand implements Command {
    public PutCommand (DataBase _db, String _key, String _value) {
        this.key = _key;
        this.value = _value;
        this.dataBase = _db;
    }

    private String key, value;
    private DataBase dataBase;

    public void run() throws FileMapException {
        if (!dataBase.getDataBase().containsKey(key)) {
            System.out.println("new");
        }
        else {
            System.out.println("overwrite");
            System.out.println(dataBase.getDataBase().remove(key));
        }
        dataBase.getDataBase().put(key, value);
        dataBase.writeDataBase();
    }
}
