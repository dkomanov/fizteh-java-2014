package ru.fizteh.fivt.students.torunova.proxy.database.actions;

import ru.fizteh.fivt.students.torunova.proxy.database.TableHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 21.10.14.
 */
public class DropTable extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public DropTable(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length, writer)) {
            return false;
        }
        try {
            currentTable.getDb().removeTable(parameters[0]);
        } catch (IllegalStateException e) {
            writer.println(e.getMessage());
            return false;
        }
        if (currentTable.get() != null) {
            if (currentTable.get().getName().equals(parameters[0])) {
                currentTable.reset();
            }
        }
        writer.println("dropped");
        return true;
    }

    @Override
    public String getName() {
        return "drop";
    }
}
