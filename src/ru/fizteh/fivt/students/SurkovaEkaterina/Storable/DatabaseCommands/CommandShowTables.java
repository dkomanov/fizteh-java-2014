package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell.CommandsParser;

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
                    "show tables: Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException(
                    "Unknown command: show");
        }
        if (!(parameters[0].equals("tables"))) {
            throw new IllegalArgumentException(
                    "Unknown command: show " + params);
        }

        ops.showTables();
    }
}
