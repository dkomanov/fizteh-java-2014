package ru.fizteh.fivt.students.torunova.parallel.actions;

import ru.fizteh.fivt.students.torunova.parallel.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 02.11.14.
 */
public class Size extends Action {
    @Override
    public boolean run(String[] args, DatabaseWrapper db)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (db.getCurrentTable() == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(db.getCurrentTable().size());
        return true;
    }

    @Override
    public String getName() {
        return "size";
    }
}
