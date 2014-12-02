package ru.fizteh.fivt.students.torunova.multifilehashmap.actions;

import ru.fizteh.fivt.students.torunova.multifilehashmap.Database;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class Get extends Action {
    @Override
    public boolean run(String[] args, Database db) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.currentTable == null) {
            System.out.println("no table");
            return false;
        }
            String value = db.currentTable.get(args[0]);
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

