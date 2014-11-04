package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class UseTable extends Action{
    @Override
    public boolean run(String[] args, Database db) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.currentTable != null) {
            int numberOfUnsavedChanges = db.currentTable.countChangedEntries();
            if (numberOfUnsavedChanges != 0) {
                System.err.println(numberOfUnsavedChanges + " unsaved changes");
                return false;
            }
        }
        if (db.useTable(args[0])) {
            System.out.println("using " + args[0]);
            return true;
        } else {
            System.out.println(args[0] + " does not exist");
            return false;
        }
    }

    @Override
    public String getName() {
        return "use";
    }
}
