package ru.fizteh.fivt.students.torunova.parallel.database.actions;

import ru.fizteh.fivt.students.torunova.parallel.database.StoreableType;
import ru.fizteh.fivt.students.torunova.parallel.database.TableHolder;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by nastya on 21.10.14.
 */
public class Put extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public Put(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] arguments = new String[2];
        String[] parameters = parseArguments(args);
        arguments[0] = parameters[0];
        arguments[1] = String.join(" ", Arrays.copyOfRange(parameters, 1, parameters.length));
        if (!checkNumberOfArguments(2, arguments.length, writer)) {
            return false;
        }
        if (currentTable.get() == null) {
            writer.println("no table");
            return false;
        }
        String oldValue;
        StoreableType deserializedValue = null;
        try {
            deserializedValue = (StoreableType) currentTable.getDb().deserialize(currentTable.get(), arguments[1]);
        } catch (ParseException e) {
            //it is never thrown.
        }
        oldValue = currentTable.getDb().serialize(currentTable.get(),
                currentTable.get().put(parameters[0], deserializedValue));
        if (oldValue == null) {
            writer.println("new");
        } else {
            writer.println("overwrite");
            writer.println(oldValue);
        }
        return true;
    }

    @Override
    public String getName() {
        return "put";
    }
}

