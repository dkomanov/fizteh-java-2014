package ru.fizteh.fivt.students.torunova.proxy.database.actions;

import ru.fizteh.fivt.students.torunova.proxy.database.TableHolder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 21.10.14.
 */
public class UseTable extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public UseTable(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length, writer)) {
            return false;
        }
        if (currentTable.get() != null) {
            int numberOfUnsavedChanges = currentTable.get().getNumberOfUncommittedChanges();
            if (numberOfUnsavedChanges != 0) {
                writer.println(numberOfUnsavedChanges + " unsaved changes");
                return false;
            }
        }
        if (currentTable.set(parameters[0])) {
            writer.println("using " + parameters[0]);
            return true;
        } else {
            writer.println(parameters[0] + " does not exist");
            return false;
        }
    }

    @Override
    public String getName() {
        return "use";
    }
}
