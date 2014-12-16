package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

import ru.fizteh.fivt.students.sautin1.telnet.shell.AbstractCommand;
import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.RemoteTableProviderClient;

import java.io.IOException;

/**
 * Created by sautin1 on 10/12/14.
 */
public class RemoteDatabaseCommand extends AbstractCommand<RemoteTableDatabaseState> {

    public RemoteDatabaseCommand(String name, int minArgNumber, int maxArgNumber) {
        super(name, minArgNumber, maxArgNumber);
    }

    @Override
    public void execute(RemoteTableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException("wrong number of arguments");
        }
        if (state.getProvider() == null) {
            throw new CommandExecuteException("not connected");
        }
        if (state.getProvider().getSocket().isInputShutdown()) {
            try {
                state.getProvider().disconnect();
            } catch (IOException e) {
                throw new CommandExecuteException(e.getMessage());
            }
        }
        try {
            RemoteTableProviderClient provider = state.getProvider();
            String command = String.join(" ", args);
            state.getOutStream().println();
            String result = provider.sendCommand(command);
            state.getOutStream().println(result);
            state.getOutStream().flush();
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }


}
