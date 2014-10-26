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
     * Correct quantity of arguments of this command.
     */
    final int argumentsCount = 0;
    /**
     * Show every table and its' elements' quantity.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("show tables", args.length, argumentsCount);

        for (HashMap.Entry<String, Table> entry: db.getSet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().size());
        }

    }
}
