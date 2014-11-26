package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.DatabaseWrapper;

/**
 * Created by nastya on 21.10.14.
 */
public class Remove extends Action {
    @Override
    public boolean run(String[] args, DatabaseWrapper db) {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.getCurrentTable() == null) {
            System.out.println("no table");
            return false;
        }
        String result = db.serialize(db.getCurrentTable(), db.getCurrentTable().get(args[0]));
        db.getCurrentTable().remove(args[0]);
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
