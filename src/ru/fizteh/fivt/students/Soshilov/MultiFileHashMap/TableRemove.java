package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:40
 */
public class TableRemove implements Command {
    /**
     * Remove key + value by getting the key as argument.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("remove", args.length, 2);

        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }

        String key = args[1];
        String value = db.currentTable.remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }

    }
}
