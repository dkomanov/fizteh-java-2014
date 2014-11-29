package ru.fizteh.fivt.students.torunova.parallel.actions;

import ru.fizteh.fivt.students.torunova.parallel.DatabaseWrapper;

import java.util.List;

/**
 * Created by nastya on 21.10.14.
 */
public class MyList extends Action {
    @Override
    public boolean run(String[] args, DatabaseWrapper db) {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (db.getCurrentTable() == null) {
            System.out.println("no table");
            return false;
        }
        List<String> keys = db.getCurrentTable().list();
        String result = String.join(", ", keys);
        System.out.println(result);
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }
}
