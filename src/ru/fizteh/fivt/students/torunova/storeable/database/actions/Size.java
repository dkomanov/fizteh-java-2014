package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;

/**
 * Created by nastya on 02.11.14.
 */
public class Size extends Action {
    @Override
    public boolean run(String args, TableHolder currentTable)
            throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(0, parameters.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(currentTable.get().size());
        return true;
    }

    @Override
    public String getName() {
        return "size";
    }
}
