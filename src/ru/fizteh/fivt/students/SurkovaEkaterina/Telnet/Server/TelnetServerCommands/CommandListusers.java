package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServerCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServer;

import java.io.PrintStream;
import java.util.ArrayList;

public class CommandListusers extends ACommand<TelnetServer> {

    public CommandListusers() {
        super("listusers", "listusers");
    }

    public final void executeCommand(final String params,
                                     final TelnetServer server,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 0) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        ArrayList<String> result = new ArrayList<>(server.listUsers());
        if (result == null) {
            out.println("not started");
        } else {
            out.println(String.join("\n", result));
        }
    }
}
