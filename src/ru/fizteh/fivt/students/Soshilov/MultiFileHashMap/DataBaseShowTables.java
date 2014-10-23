package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:56
 */
public class DataBaseShowTables implements Command {
    /**
     * Show every table and its' elements' quantity.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        if (!args[1].equals("tables")) {
            System.err.println(args[0] + " " + args[1] + ": command not found");
        }

        Main.checkArguments("show tables", args.length, 2);

        System.out.println("table_name row_count");

        for (HashMap.Entry<String, Table> entry: db.tables.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().size());
        }

    }
}
