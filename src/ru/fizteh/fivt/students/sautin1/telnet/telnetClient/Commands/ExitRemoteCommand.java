package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;


import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableProviderClient;

/**
 * "exit" command.
 * Created by sautin1 on 9/30/14.
 */
public class ExitRemoteCommand extends RemoteDatabaseCommand {

    public ExitRemoteCommand() {
        super("exit", 0, 0);
    }

    @Override
    public void execute(RemoteTableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException("wrong number of arguments");
        }
        try {
            RemoteTableProviderClient provider = state.getProvider();
            if (provider != null) {
                String command = args[0];
                String result = provider.sendCommand(command);
                state.getOutStream().println(result);
                state.getOutStream().flush();
            }
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
        throw new UserInterruptException();
    }
}
