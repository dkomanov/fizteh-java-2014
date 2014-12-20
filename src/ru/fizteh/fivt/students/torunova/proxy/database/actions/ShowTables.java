package ru.fizteh.fivt.students.torunova.proxy.database.actions;

import ru.fizteh.fivt.students.torunova.proxy.database.TableHolder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by nastya on 21.10.14.
 */
public class ShowTables extends Action {
    TableHolder currentTable;
    PrintWriter writer;
    public ShowTables(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args)
            throws IOException {
        String[] parameters = parseArguments(args);
        if (parameters.length > 1) {
            tooManyArguments(writer);
            return false;
        } else if (parameters.length < 1) {
            writer.println("Command not found");
            return false;
        } else if (!parameters[0].equals("tables")) {
            writer.println("Command not found");
            return false;
        } else {
            writer.println(String.format("%s %s", "table_name", "row_count"));
            Map<String, Integer> tables = currentTable.getDb().showTables();
            tables.forEach((tableName, rowCount)->writer.println(String.format("%s %s", tableName, rowCount)));
            return true;
        }

    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDisplayName() {
        return "show tables";
    }
}
