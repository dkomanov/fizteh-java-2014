package ru.fizteh.fivt.students.torunova.proxy.actions;

import ru.fizteh.fivt.students.torunova.proxy.CurrentTable;
import ru.fizteh.fivt.students.torunova.proxy.exceptions.IncorrectFileException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by nastya on 21.10.14.
 */
public class Put extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) throws IOException, IncorrectFileException {
        String[] arguments = new String[2];
        arguments[0] = args[0];
        arguments[1] = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (!checkNumberOfArguments(2, arguments.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        String oldValue = null;
        try {
            oldValue = currentTable.getDb().serialize(currentTable.get(), currentTable.get().put(args[0],
                    currentTable.getDb().deserialize(currentTable.get(), args[1])));
        } catch (ParseException e) {
            //it is never thrown.
        }
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
