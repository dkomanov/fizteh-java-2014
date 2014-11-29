package ru.fizteh.fivt.students.torunova.parallel.actions;

import ru.fizteh.fivt.students.torunova.parallel.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class Get extends Action {
    @Override
    public boolean run(String[] args, DatabaseWrapper db) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.getCurrentTable() == null) {
            System.out.println("no table");
            return false;
        }
            String value = db.serialize(db.getCurrentTable(), db.getCurrentTable().get(args[0]));
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "get";
    }
}

