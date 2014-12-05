package ru.fizteh.fivt.students.torunova.proxy.actions;

import ru.fizteh.fivt.students.torunova.proxy.CurrentTable;
import ru.fizteh.fivt.students.torunova.proxy.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.proxy.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Exit extends Action {


    @Override
    public boolean run(String[] args, CurrentTable currentTable)
            throws IOException, IncorrectFileException, TableNotCreatedException {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (currentTable.get() != null) {
            currentTable.get().commit();
        }
        System.exit(0);
        return true;
    }
    @Override
    public String getName() {
        return "exit";
    }
}
