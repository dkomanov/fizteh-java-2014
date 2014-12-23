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
public class WhereamiRemoteCommand extends RemoteDatabaseCommand {

    public WhereamiRemoteCommand() {
        super("whereami", 0, 0);
    }

    /**
     * Create new table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException if user desires to exit.
     * @throws ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(RemoteTableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (state.getProvider() == null) {
            state.getOutStream().println("local");
        } else {
            state.getOutStream().println("remote " + state.getProvider().getSocket().getInetAddress().getHostAddress()
                    + " " + state.getProvider().getSocket().getPort());
        }
    }
}
