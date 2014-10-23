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
     * Creates new table.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("create", args.length, 2);

        String tableName = args[1];
        Path tablePath = db.getDbPath().resolve(tableName);
        //Converts a given string to a Path and resolves it against this Path

        if (db.tables.put(tableName, new Table(tablePath)) == null) {
            System.out.println("created");
        } else {
            System.out.println("tablename exists");
        }

    }
}
