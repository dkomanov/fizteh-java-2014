package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:16
 */
public class TablePut implements Command {
    /**
     * Correct quantity of arguments of this command.
     */
    final int argumentsCount = 2;
    /**
     * Put a key + value into a table.
     * @param args Commands that were entered.
     * @param db Our main table.
     * @throws CommandException Error in wrong arguments count.
     */
    @Override
    public void execute(final String[] args, DataBase db) throws CommandException {
        Main.checkArguments("put", args.length, argumentsCount);

        if (db.currentTableExists()) {
            System.out.println("no table");
            return;
        }

        String key = args[0];
        String value = args[1];
        if (db.putKeyAndValueIntoCurrentTable(key, value) != null) {
            System.out.println("overwrite");
        } else {
            System.out.println("new");
        }
    }
}
