package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.CurrentTable;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.TableNotCreatedException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nastya on 21.10.14.
 */
public class ShowTables extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable)
                               throws IOException,
                               IncorrectFileException,
                               TableNotCreatedException {
        if (args.length > 1) {
            tooManyArguments();
            return false;
        } else if (args.length < 1) {
            System.err.println("Command not found");
            return false;
        } else if (!args[0].equals("tables")) {
            System.err.println("Command not found");
            return false;
        } else {
            System.out.println("table_name row_count");
            Map<String, Integer> tables = currentTable.getDb().showTables();
            tables.forEach((tableName, rowCount)->System.out.println(tableName + " " + rowCount));
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
