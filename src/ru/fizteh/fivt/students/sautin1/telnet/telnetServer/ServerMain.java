package ru.fizteh.fivt.students.sautin1.telnet.telnetServer;

import ru.fizteh.fivt.students.sautin1.telnet.shell.Command;
import ru.fizteh.fivt.students.sautin1.telnet.shell.Shell;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands.ListUsersCommand;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands.StartCommand;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands.StopCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Main class.
 * Created by sautin1 on 10/12/14.
 */
public class ServerMain {

    public static void main(String[] args) {
        int exitStatus = 0;
        try {
            System.setProperty(ServerClientResponder.DB_LOCATION_PROPERTY,
                    "/home/sautin1/IdeaProjects/MIPTProjects/src/ru/fizteh/fivt/students/sautin1/test");
            if (System.getProperty(ServerClientResponder.DB_LOCATION_PROPERTY) == null) {
                throw new IllegalArgumentException("No system property \'"
                        + ServerClientResponder.DB_LOCATION_PROPERTY + "\' found");
            }
            ServerState serverState = new ServerState(new BufferedReader(new InputStreamReader(System.in)),
                    new PrintWriter(System.out, true));

            Command[] commands = {
                    new StartCommand(), new StopCommand(), new ListUsersCommand(),
            };
            @SuppressWarnings("unchecked")
            Shell<ServerState> shell = new Shell<>(serverState, commands);
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
