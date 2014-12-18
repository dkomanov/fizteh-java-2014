package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 21.10.14.
 */
public class Get extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public Get(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length, writer)) {
            return false;
        }
        if (currentTable.get() == null) {
            writer.println("no table");
            return false;
        }
            String value = currentTable.getDb().serialize(currentTable.get(), currentTable.get().get(parameters[0]));
        if (value == null) {
            writer.println("not found");
        } else {
            writer.println("found");
            writer.println(value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "get";
    }
}

