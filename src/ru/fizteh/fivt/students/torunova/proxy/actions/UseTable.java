package ru.fizteh.fivt.students.torunova.proxy.actions;

import ru.fizteh.fivt.students.torunova.proxy.CurrentTable;
import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class UseTable extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) throws IOException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (currentTable.get() != null) {
            int numberOfUnsavedChanges = currentTable.get().getNumberOfUncommittedChanges();
            if (numberOfUnsavedChanges != 0) {
                System.err.println(numberOfUnsavedChanges + " unsaved changes");
                return false;
            }
        }
        if (currentTable.set(args[0])) {
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
