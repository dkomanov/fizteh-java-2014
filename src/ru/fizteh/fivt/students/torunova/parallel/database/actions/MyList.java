package ru.fizteh.fivt.students.torunova.parallel.database.actions;

import ru.fizteh.fivt.students.torunova.parallel.database.TableHolder;
import ru.fizteh.fivt.students.torunova.storeable.database.actions.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by nastya on 21.10.14.
 */
public class MyList extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public MyList(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(0, parameters.length, writer)) {
            return false;
        }
        if (currentTable.get() == null) {
            writer.println("no table");
            return false;
        }
        List<String> keys = currentTable.get().list();
        String result = String.join(", ", keys);
        writer.println(result);
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }
}
