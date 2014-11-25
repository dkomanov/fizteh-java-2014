package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 23 October 2014
 * Time: 1:51
 */
public class DataBaseDrop implements Command {
    /**
     * Drop (destroy) table.
     * @param args Commands that were entered.
     * @param db Our main table.
     * @throws CommandException Error in wrong arguments count.
     */
    @Override
    public void execute(final String[] args, DataBase db) throws CommandException {
        final int argumentsCount = 1;
        Main.checkArguments("drop", args.length, argumentsCount);

        if (db.removeTable(args[0]) == null) {
            System.out.println("tablename does not exists");
        } else {
            System.out.println("dropped");
        }

    }
}
