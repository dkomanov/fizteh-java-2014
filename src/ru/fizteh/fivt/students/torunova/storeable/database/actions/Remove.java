package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 21.10.14.
 */
public class Remove extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public Remove(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length, writer)) {
            return false;
        }
        if (currentTable.get() == null) {
            writer.println("no table");
            return false;
        }
        String result = currentTable.getDb().serialize(currentTable.get(), currentTable.get().get(parameters[0]));
        currentTable.get().remove(parameters[0]);
        if (result != null) {
            writer.println("removed");
        } else {
            writer.println("not found");
        }
        return true;
    }

    @Override
    public String getName() {
        return "remove";
    }
}
