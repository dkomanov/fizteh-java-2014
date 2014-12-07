package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class Put extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(2, args.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        String oldValue = currentTable.get().put(args[0], args[1]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
        return true;
    }

    @Override
    public String getName() {
        return "put";
    }
}
