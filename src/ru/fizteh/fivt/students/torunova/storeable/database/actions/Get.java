package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class Get extends Action {
    @Override
    public boolean run(String args, TableHolder currentTable) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
            String value = currentTable.getDb().serialize(currentTable.get(), currentTable.get().get(parameters[0]));
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

