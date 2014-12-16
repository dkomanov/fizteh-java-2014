package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Created by sautin1 on 10/20/14.
 */

import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableDatabaseState;

/**
 * Command for deleting database table.
 * Created by sautin1 on 10/20/14.
 */
public class DisconnectRemoteCommand extends RemoteDatabaseCommand {

    public DisconnectRemoteCommand() {
        super("disconnect", 0, 0);
    }

    /**
     * Create new table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(RemoteTableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException("wrong number of arguments");
        }
        if (state.getProvider() == null) {
            throw new CommandExecuteException("not connected");
        }
        try {
            state.getProvider().disconnect();
            state.setProvider(null);
            state.getOutStream().println("disconnected");
            state.getOutStream().flush();
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
