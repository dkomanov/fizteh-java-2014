package ru.fizteh.fivt.students.sautin1.telnet.multifilemap;

/**
 * Created by sautin1 on 10/20/14.
 */

import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;

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
            state.getOutStream().println("dropped");
        } catch (IllegalArgumentException e) {
            throw new CommandExecuteException(args[1] + " not exists");
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
