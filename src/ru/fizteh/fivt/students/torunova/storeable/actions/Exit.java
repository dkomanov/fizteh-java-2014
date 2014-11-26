package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Exit extends Action {


    @Override
    public boolean run(String[] args, DatabaseWrapper db)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (db.getCurrentTable() != null) {
            db.getCurrentTable().commit();
        }
        System.exit(0);
        return true;
    }
    @Override
    public String getName() {
        return "exit";
    }
}
