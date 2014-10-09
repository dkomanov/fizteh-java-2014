package ru.fizteh.fivt.students.PoatpovaSofia.FileMap;

import java.util.Iterator;
import java.util.Set;

public class Command {
    public static void put(DataBase db, String key, String value) throws Exception {
        if (!db.getDataBase().containsKey(key)) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(db.getDataBase().get(key));
            db.getDataBase().remove(key);
        }
        db.getDataBase().put(key, value);
        db.writeDataToFile();
    }
    public static void get(DataBase db, String key) throws Exception {
        if (db.getDataBase().containsKey(key)) {
            System.out.println("found");
            System.out.println(db.getDataBase().get(key));
        } else {
            System.out.println("not found");
        }
    }
    public static void remove(DataBase db, String key) throws Exception {
        if (db.getDataBase().containsKey(key)) {
            db.getDataBase().remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        db.writeDataToFile();
    }
    public static void list(DataBase db) {
        Set keySet = db.getDataBase().keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();
    }
}
