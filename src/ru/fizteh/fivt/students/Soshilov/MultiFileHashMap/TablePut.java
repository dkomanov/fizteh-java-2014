package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:16
 */
public class TablePut implements Command {
    /**
     * Put a key + value into a table.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("put", args.length, 3);

        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }

        String key = args[1];
        String value = args[2];
        if (db.currentTable.put(key, value) != null) {
            System.out.println("overwrite");
        } else {
            System.out.println("new");
        }
    }
}
