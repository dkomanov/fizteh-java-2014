package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServerCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServer;

import java.io.PrintStream;

public class CommandStart extends ACommand<TelnetServer> {
    private static final int DEFAULT_PORT = 10001;

    public CommandStart() {
        super("start", "start <port>");
    }

    public final void executeCommand(String params,
                                     TelnetServer server,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        int port;
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        try {
            port = (parameters.length == 0) ? DEFAULT_PORT : Integer.parseInt(parameters[0]);
        } catch (NumberFormatException e) {
            out.println(getClass().getSimpleName() + ": Incorrect port number!");
            return;
        }
        try {
            server.start(port);
        } catch (Exception e) {
            out.println("not started: " + e.getMessage());
            return;
        }
        out.println("started at " + port);
    }
}
