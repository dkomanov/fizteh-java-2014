package ru.fizteh.fivt.students.sautin1.telnet.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;

/**
 * Remove command.
 * Created by sautin1 on 10/12/14.
 */
public class RemoveCommand extends AbstractStoreableDatabaseCommand {

    public RemoveCommand() {
        super("remove", 1, 1);
    }

    /**
     * Remove entry by its key from the active table.
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
        GeneralTable<Storeable> table = state.getActiveTable();
        if (table != null) {
            Storeable value = table.remove(args[1]);
            if (value == null) {
                state.getOutStream().println("not found");
            } else {
                state.getOutStream().println("removed");
            }
        } else {
            throw new CommandExecuteException("no table");
        }
    }
}
