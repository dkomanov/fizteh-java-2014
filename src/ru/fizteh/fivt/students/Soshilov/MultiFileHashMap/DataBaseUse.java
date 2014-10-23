package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:53
 */
public class DataBaseUse implements Command {
    /**
     * Change current table and use a new one.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("use", args.length, 2);

        Table table = db.tables.get(args[1]);
        if (table == null) {
            System.out.println("tablename does not exists");
        } else {
            db.currentTable = table;
        }

    }
}
