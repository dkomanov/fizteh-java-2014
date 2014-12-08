package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class DropTable extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        try {
            currentTable.getDb().removeTable(args[0]);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return false;
        }
        if (currentTable.get() != null) {
            if (currentTable.get().getName().equals(args[0])) {
                currentTable.reset();
            }
        }
        System.out.println("dropped");
        return true;
    }

    @Override
    public String getName() {
        return "drop";
    }
}
