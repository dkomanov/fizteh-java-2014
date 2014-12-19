package ru.fizteh.fivt.students.torunova.parallel.database.actions;

import ru.fizteh.fivt.students.torunova.parallel.database.TableHolder;
import ru.fizteh.fivt.students.torunova.storeable.database.actions.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 02.11.14.
 */
public class Size extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public Size(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args)
            throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(0, parameters.length, writer)) {
            return false;
        }
        if (currentTable.get() == null) {
            writer.println("no table");
            return false;
        }
        writer.println(currentTable.get().size());
        return true;
    }

    @Override
    public String getName() {
        return "size";
    }
}
