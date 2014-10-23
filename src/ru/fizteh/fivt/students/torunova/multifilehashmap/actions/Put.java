package ru.fizteh.fivt.students.torunova.multifilehashmap.actions;

import ru.fizteh.fivt.students.torunova.multifilehashmap.Database;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class Put extends Action {
    @Override
    public boolean run(String[] args, Database db) throws IOException, IncorrectFileException {
        if (args.length < 2) {
            tooFewArguments();
            return false;
        } else if (args.length > 2) {
            tooManyArguments();
            return false;
        }
        if (db.currentTable == null) {
            System.out.println("no table");
            return false;
        }
        String value = db.currentTable.get(args[0]);
        boolean result = db.currentTable.put(args[0], args[1]);
        if (result) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "put";
    }
}
