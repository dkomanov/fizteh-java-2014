package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;

import java.io.PrintStream;

public class CommandExit<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                                     final TableOperations ops,
                                     PrintStream out) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many parameters!");
        }
        ops.rollback();
        System.exit(ops.exit());
    }
}
