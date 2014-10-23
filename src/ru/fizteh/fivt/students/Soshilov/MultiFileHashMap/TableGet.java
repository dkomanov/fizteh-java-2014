package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:26
 */
public class TableGet implements Command {
    /**
     * Get the value by having a key.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("get", args.length, 2);

        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }

        String key = args[1];
        String value = db.currentTable.get(key);
        if (value != null) {
            System.out.println("found\n" + value);
        } else {
            System.out.println("not found");
        }

    }
}
