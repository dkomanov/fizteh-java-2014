package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nastya on 21.10.14.
 */
public class ShowTables extends Action {
    TableHolder currentTable;
    public ShowTables(TableHolder currentTable) {
        this.currentTable = currentTable;
    }
    @Override
    public boolean run(String args)
                               throws IOException {
        String[] parameters = parseArguments(args);
        if (parameters.length > 1) {
            tooManyArguments();
            return false;
        } else if (parameters.length < 1) {
            System.err.println("Command not found");
            return false;
        } else if (!parameters[0].equals("tables")) {
            System.err.println("Command not found");
            return false;
        } else {
            System.out.println(String.format("%s%20s", "table_name", "row_count"));
            Map<String, Integer> tables = currentTable.getDb().showTables();
            tables.forEach((tableName, rowCount)->System.out.println(String.format("%s%20s", tableName, rowCount)));
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
