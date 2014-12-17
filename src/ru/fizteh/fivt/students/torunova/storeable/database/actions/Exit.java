package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Exit extends Action {


    @Override
    public boolean run(String args, TableHolder currentTable)
            throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(0, parameters.length)) {
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
