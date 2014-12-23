package ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands;

import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.ServerState;

import java.net.InetAddress;
import java.util.List;

/**
 * Get command.
 * Created by sautin1 on 10/12/14.
 */
public class ListUsersCommand extends ServerCommand {

    public ListUsersCommand() {
        super("listusers", 0, 0);
    }

    /**
     * Get value by key from the active table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ServerState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        try {
            if (!state.isServerStarted()) {
                throw new CommandExecuteException("not started");
            }
            List<InetAddress> userList = state.listUsers();
            for (InetAddress user : userList) {
                state.getOutStream().println(user.toString());
                state.getOutStream().flush();
            }
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
