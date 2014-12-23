package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;

import java.io.PrintStream;

public class CommandUse<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandUse() {
        super("use", "use <table name>");
    }

    public final void executeCommand(String params,
                                     TableOperations ops,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        Table newTable = null;
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }
        try {
            newTable = ops.useTable(parameters[0]);
        } catch (IllegalStateException e) {
            out.println(String.format("wrong type (%s)", e.getMessage()));
            return;
        }

        if (newTable == null) {
            out.println(String.format("%s not exists", parameters[0]));
            return;
        }

        out.println(String.format("using %s", ops.getActiveTableName()));
    }
}

