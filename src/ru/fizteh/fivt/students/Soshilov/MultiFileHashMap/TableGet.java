package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:26
 */
public class TableGet implements Command {
    /**
     * Correct quantity of arguments of this command.
     */
    final int argumentsCount = 1;
    /**
     * Get the value by having a key.
     * @param args Commands that were entered.
     * @param db Our main table.
     * @throws CommandException Error in wrong arguments count.
     */
    @Override
    public void execute(final String[] args, DataBase db) throws CommandException {
        Main.checkArguments("get", args.length, argumentsCount);

        if (db.currentTableExists()) {
            System.out.println("no table");
            return;
        }

        String key = args[1];
        String value = db.getValueFromCurrentTable(key);
        if (value != null) {
            System.out.println("found\n" + value);
        } else {
            System.out.println("not found");
        }

    }
}
