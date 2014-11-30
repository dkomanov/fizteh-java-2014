package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.CurrentTable;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Commit extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (checkNumberOfArguments(0, args.length)) {
            if (currentTable.get() == null) {
                System.err.println("no table");
                return false;
            }
            System.out.println(currentTable.get().commit());
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "commit";
    }
}
