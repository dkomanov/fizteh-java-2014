package ru.fizteh.fivt.students.sautin1.telnet.telnetClient;


import ru.fizteh.fivt.students.sautin1.telnet.shell.Command;
import ru.fizteh.fivt.students.sautin1.telnet.shell.Shell;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Main class.
 * Created by sautin1 on 10/12/14.
 */
public class ClientMain {
    public static void main(String[] args) {
        int exitStatus = 0;
        try {
            RemoteTableProviderFactoryClient providerFactory = new RemoteTableProviderFactoryClient();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter outStream = new PrintWriter(System.out, true);
            RemoteTableDatabaseState databaseState = new RemoteTableDatabaseState(providerFactory, inStream, outStream);

            Command[] commands = {
                    new ConnectRemoteCommand(), new DisconnectRemoteCommand(), new WhereamiRemoteCommand(),
                    new PutRemoteCommand(), new GetRemoteCommand(), new ListRemoteCommand(),
                    new RemoveRemoteCommand(), new CreateRemoteCommand(), new DropRemoteCommand(),
                    new ShowTablesRemoteCommand(), new UseRemoteCommand(), new CommitRemoteCommand(),
                    new RollbackRemoteCommand(), new SizeRemoteCommand(), new ExitRemoteCommand()
            };
            @SuppressWarnings("unchecked")
            Shell<RemoteTableDatabaseState> shell = new Shell<>(databaseState, commands);
            try {
                shell.startWork(args);
            } catch (UserInterruptException e) {
                exitStatus = 0;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            exitStatus = 1;
        }
        System.exit(exitStatus);
    }

}
