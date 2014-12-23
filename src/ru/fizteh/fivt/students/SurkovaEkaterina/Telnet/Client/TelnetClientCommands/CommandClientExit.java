package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClient;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;

import java.io.PrintStream;

public class CommandClientExit<Operator extends DatabaseShellOperationsInterface>
        extends ACommand<Operator> {
    public CommandClientExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                                     final Operator client,
                                     PrintStream out) {

        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many parameters!");
        }
        if (((TelnetClient) client).works()) {
            client.rollback();
        }
        System.exit(client.exit());
    }
}
