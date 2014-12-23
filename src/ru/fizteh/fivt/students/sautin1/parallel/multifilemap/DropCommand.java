package ru.fizteh.fivt.students.sautin1.parallel.multifilemap;

/**
 * Created by sautin1 on 10/20/14.
 */

import ru.fizteh.fivt.students.sautin1.parallel.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableDatabaseState;

/**
 * Command for deleting database table.
 * Created by sautin1 on 10/20/14.
 */
public class DropCommand extends AbstractStoreableDatabaseCommand {

    public DropCommand() {
        super("drop", 1, 1);
    }

    /**
     * Create new table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StoreableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        try {
            if (state.getActiveTable() != null && args[1].equals(state.getActiveTable().getName())) {
                state.setActiveTable(null);
            }
            state.getTableProvider().removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalArgumentException e) {
            System.err.println(args[1] + " not exists");
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
