package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableProviderClient;

/**
 * Command for connecting to remote database table.
 * Created by sautin1 on 10/20/14.
 */
public class ConnectRemoteCommand extends RemoteDatabaseCommand {

    public ConnectRemoteCommand() {
        super("connect", 2, 2);
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
        try {
            RemoteTableProviderClient provider = state.getFactory().connect(args[1], Integer.parseInt(args[2]));
            if (provider == null) {
                throw new CommandExecuteException("null provider");
            }
            state.setProvider(provider);
            state.getOutStream().println("connected");
            state.getOutStream().flush();
        } catch (Exception e) {
            throw new CommandExecuteException("not connected: " + e.getMessage());
        }
    }
}
