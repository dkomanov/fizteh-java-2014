package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.Database;

/**
 * Created by nastya on 21.10.14.
 */
public class Remove extends Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.currentTable == null) {
            System.out.println("no table");
            return false;
        }
        String result = db.currentTable.remove(args[0]);
        if (result != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }

    @Override
    public String getName() {
        return "remove";
    }
}
