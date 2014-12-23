package ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands;

import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.ServerState;

/**
 * Put command.
 * Created by sautin1 on 10/12/14.
 */
public class StartCommand extends ServerCommand {

    public StartCommand() {
        super("start", 0, 1);
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
            int port;
            if (args.length == 1) {
                port = 10001;
            } else {
                port = Integer.parseInt(args[1]);
            }
            if (state.isServerStarted()) {
                throw new CommandExecuteException("already started");
            }
            port = state.launchConnectionManager(port);
            state.getOutStream().println("started at port " + port);
            state.getOutStream().flush();
        } catch (Exception e) {
            throw new CommandExecuteException("not started: " + e.getMessage());
        }
    }
}
