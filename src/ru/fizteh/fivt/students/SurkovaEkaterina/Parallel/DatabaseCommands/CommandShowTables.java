package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.CommandsParser;

public class CommandShowTables<Table, Key, Value, TableOperations
        extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandShowTables() {
        super("show", "show tables");
    }

    public final void executeCommand(final String params,
                                     final TableOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(
                    this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException(
                    this.getClass().toString() + ": Unknown command: show");
        }
        if (!(parameters[0].equals("tables"))) {
            throw new IllegalArgumentException(
                    this.getClass().toString() + ": Unknown command: show " + params);
        }

        ops.showTables();
    }
}
