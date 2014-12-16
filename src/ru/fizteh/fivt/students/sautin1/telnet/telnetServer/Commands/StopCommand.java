package ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands;

import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.ServerState;

/**
 * Put command.
 * Created by sautin1 on 10/12/14.
 */
public class StopCommand extends ServerCommand {

    public StopCommand() {
        super("stop", 0, 1);
    }

    /**
     * Put new key and value to the active table.
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
            int port = state.stopConnectionManager();
            state.getOutStream().println("stopped at port " + port);
            state.getOutStream().flush();
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
