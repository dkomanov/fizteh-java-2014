package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell.ACommand;

public class CommandExit<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                                     final TableOperations ops) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many parameters!");
        }
        ops.rollback();
        System.exit(ops.exit());
    }
}
