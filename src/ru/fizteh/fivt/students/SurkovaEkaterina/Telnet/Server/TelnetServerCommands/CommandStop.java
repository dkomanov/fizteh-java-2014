package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServerCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServer;

import java.io.PrintStream;

public class CommandStop extends ACommand<TelnetServer> {

    public CommandStop() {
        super("stop", "stop");
    }

    public final void executeCommand(String params,
                                     TelnetServer server,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 0) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        int result = 0;
        try {
            result = server.stop();
        } catch (Exception e) {
            out.println("Cannot stop server!");
        }
        if (result == -1) {
            out.println("not started");
        } else {
            out.println("stopped at " + result);
        }
    }
}
