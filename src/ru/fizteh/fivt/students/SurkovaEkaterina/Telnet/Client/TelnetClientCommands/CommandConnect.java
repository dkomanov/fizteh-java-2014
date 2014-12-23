package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClient;

import java.io.PrintStream;

public class CommandConnect extends ACommand<TelnetClient> {

    public CommandConnect() {
        super("connect", "connect <hostport>");
    }

    public final void executeCommand(final String params,
                                     TelnetClient client,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        String host;
        int port;
        if (parameters.length > 2) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Too many arguments!");
        }
        if (parameters.length < 2) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Needs more parameters!");
        }
        host = parameters[0];
        try {
            port = Integer.parseInt(parameters[1]);
        } catch (NumberFormatException e) {
            out.println(getClass().getSimpleName() + "Incorrect host port number!");
            return;
        }
        try {
            client.connect(host, port);
        } catch (Exception e) {
            out.println("not connected: " + e.getMessage());
            return;
        }
        out.println("connected");
    }
}
