package ru.fizteh.fivt.students.sautin1.telnet.junit;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.AbstractStoreableDatabaseCommand;

/**
 * Size command.
 * Created by sautin1 on 10/12/14.
 */
public class SizeCommand extends AbstractStoreableDatabaseCommand {

    public SizeCommand() {
        super("size", 0, 0);
    }

    /**
     * Get the number of entries in the active table.
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
            state.getOutStream().println(table.size());
        } else {
            throw new CommandExecuteException("no table");
        }
    }
}
