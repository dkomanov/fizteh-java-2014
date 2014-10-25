package ru.fizteh.fivt.students.torunova.multifilehashmap.actions;

import ru.fizteh.fivt.students.torunova.multifilehashmap.Database;

import java.util.Set;

/**
 * Created by nastya on 21.10.14.
 */
public class List extends Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (db.currentTable == null) {
            System.out.println("no table");
            return false;
        }
        Set<String> keys = db.currentTable.list();
        String result = String.join(", ", keys);
        System.out.println(result);
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }
}
