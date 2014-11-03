package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:40
 */
public class TableRemove implements Command {
    /**
     * Correct quantity of arguments of this command.
     */
    final int argumentsCount = 1;
    /**
     * Remove key + value by getting the key as argument.
     * @param args Commands that were entered.
     * @param db Our main table.
     * @throws CommandException Error in wrong arguments count.
     */
    @Override
    public void execute(final String[] args, DataBase db) throws CommandException {
        Main.checkArguments("remove", args.length, argumentsCount);

        if (db.currentTableExists()) {
            System.out.println("no table");
            return;
        }

        String key = args[0];
        String value = db.removeKeyAndValueFromCurrentTable(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }

    }
}
