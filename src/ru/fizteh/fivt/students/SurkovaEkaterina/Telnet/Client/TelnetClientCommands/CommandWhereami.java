package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClient;

import java.io.PrintStream;

public class CommandWhereami extends ACommand<TelnetClient> {

    public CommandWhereami() {
        super("whereami", "whereami");
    }

    public final void executeCommand(String params,
                                     TelnetClient client,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Too many arguments!");
        }
        out.println(client.state.toString());
    }
}
