package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class UseTable extends Action{
    TableHolder currentTable;
    public UseTable(TableHolder currentTable) {
        this.currentTable = currentTable;
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length)) {
            return false;
        }
        if (currentTable.get() != null) {
            int numberOfUnsavedChanges = currentTable.get().getNumberOfUncommittedChanges();
            if (numberOfUnsavedChanges != 0) {
                System.err.println(numberOfUnsavedChanges + " unsaved changes");
                return false;
            }
        }
        if (currentTable.set(parameters[0])) {
            System.out.println("using " + parameters[0]);
            return true;
        } else {
            System.out.println(parameters[0] + " does not exist");
            return false;
        }
    }

    @Override
    public String getName() {
        return "use";
    }
}
