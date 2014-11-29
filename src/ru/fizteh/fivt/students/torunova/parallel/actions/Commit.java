package ru.fizteh.fivt.students.torunova.parallel.actions;

import ru.fizteh.fivt.students.torunova.parallel.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Commit extends Action {
    @Override
    public boolean run(String[] args, DatabaseWrapper db)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (checkNumberOfArguments(0, args.length)) {
            if (db.getCurrentTable() == null) {
                System.err.println("no table");
                return false;
            }
            System.out.println(db.getCurrentTable().commit());
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "commit";
    }
}
