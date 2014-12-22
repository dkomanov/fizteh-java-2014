package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.ACommand;

public class CommandExit<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                                     final TableOperations ops) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("exit: Too many parameters!");
        }
        ops.rollback();
        System.exit(ops.exit());
    }
}
