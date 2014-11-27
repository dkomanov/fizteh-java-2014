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
     * @throws CommandException Error in wrong arguments count.
     */
    @Override
    public void execute(final String[] args, DataBase db) throws CommandException {
        final int argumentsCount = 0;
        Main.checkArguments("show tables", args.length, argumentsCount);

        for (HashMap.Entry<String, Table> entry: db.getSet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().size());
        }

    }
}
