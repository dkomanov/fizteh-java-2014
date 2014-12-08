package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Rollback extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (checkNumberOfArguments(0, args.length)) {
            if (currentTable.get() == null) {
                System.err.println("no table");
                return false;
            }
            System.out.println(currentTable.get().rollback());
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "rollback";
    }
}
