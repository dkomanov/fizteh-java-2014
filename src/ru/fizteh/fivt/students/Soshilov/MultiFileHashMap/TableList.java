package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:30
 */
public class TableList implements Command {
    /**
     * List whole table.
     * @param args Commands that were entered.
     * @param db Our main table.
     */
    @Override
    public void execute(final String[] args, DataBase db) {
        Main.checkArguments("list", args.length, 1);

        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }

        int counter = 0;
        for (String s : db.currentTable.keySet()) {
            ++counter;
            if (counter == db.currentTable.keySet().size() - 1) {
                System.out.print(s + ", ");
            } else {
                System.out.print(s);
            }

        }

        System.out.println("");

    }
}
