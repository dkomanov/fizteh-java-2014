package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

/**
 * Created by nastya on 21.10.14.
 */
public class Remove extends Action {
    @Override
    public boolean run(String args, TableHolder currentTable) {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        String result = currentTable.getDb().serialize(currentTable.get(), currentTable.get().get(parameters[0]));
        currentTable.get().remove(parameters[0]);
        if (result != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }

    @Override
    public String getName() {
        return "remove";
    }
}
