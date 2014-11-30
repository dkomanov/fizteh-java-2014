package ru.fizteh.fivt.students.torunova.parallel.actions;

import ru.fizteh.fivt.students.torunova.parallel.CurrentTable;

/**
 * Created by nastya on 21.10.14.
 */
public class Remove extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        String result = currentTable.getDb().serialize(currentTable.get(), currentTable.get().get(args[0]));
        currentTable.get().remove(args[0]);
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
