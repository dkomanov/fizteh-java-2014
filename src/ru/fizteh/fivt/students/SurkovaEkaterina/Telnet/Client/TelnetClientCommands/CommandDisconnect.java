package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClient;

import java.io.IOException;
import java.io.PrintStream;

public class CommandDisconnect extends ACommand<TelnetClient> {

    public CommandDisconnect() {
        super("stop", "stop");
    }

    public final void executeCommand(String params,
                                     TelnetClient client,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 0) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        int result = 0;
        try {
            result = client.disconnect();
        } catch (IOException e) {
            out.println(getClass().getSimpleName() + ": Cannot disconnect!");
        }
        if (result == -1) {
            out.println("not connected");
        } else {
            out.println("disconnected");
        }
    }
}
