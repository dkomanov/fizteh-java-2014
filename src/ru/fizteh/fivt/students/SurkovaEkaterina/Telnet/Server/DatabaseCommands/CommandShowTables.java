package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;

import java.io.PrintStream;

public class CommandShowTables<Table, Key, Value, TableOperations
        extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandShowTables() {
        super("show", "show tables");
    }

    public final void executeCommand(String params,
                                     TableOperations ops,
                                     PrintStream out) {
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
