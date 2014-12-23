package ru.fizteh.fivt.students.sautin1.telnet.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableTable;

/**
 * Get command.
 * Created by sautin1 on 10/12/14.
 */
public class GetCommand extends AbstractStoreableDatabaseCommand {

    public GetCommand() {
        super("get", 1, 1);
    }

    /**
     * Get value by key from the active table.
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
        StoreableTable table = state.getActiveTable();
        if (table != null) {
            Storeable value = table.get(args[1]);
            if (value == null) {
                state.getOutStream().println("not found");
            } else {
                String serialized = "";
                try {
                    serialized = state.getTableProvider().serialize(table, value);
                } catch (Exception e) {
                    throw new CommandExecuteException("wrong type " + e.getMessage());
                }
                state.getOutStream().println("found");
                state.getOutStream().println(serialized);
            }
        } else {
            throw new CommandExecuteException("no table");
        }
    }
}
