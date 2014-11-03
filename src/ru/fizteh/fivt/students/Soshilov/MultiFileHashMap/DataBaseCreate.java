package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:44
 */
public class DataBaseCreate implements Command {
    /**
     * Correct quantity of arguments of this command.
     */
    final int argumentsCount = 1;
    /**
     * Creates new table.
     * @param args Commands that were entered.
     * @param db Our main table.
     * @throws CommandException Error in wrong arguments count.
     */
    @Override
    public void execute(final String[] args, DataBase db) throws CommandException {
        Main.checkArguments("create", args.length, argumentsCount);

        String tableName = args[0];
        Path tablePath = db.resolvePathAgainstDBPath(tableName);
        //Converts a given string to a Path and resolves it against this Path

        if (db.putKeyAndValue(tableName, new Table(tablePath)) == null) {
            System.out.println("created");
        } else {
            System.out.println("tablename exists");
        }

    }
}
