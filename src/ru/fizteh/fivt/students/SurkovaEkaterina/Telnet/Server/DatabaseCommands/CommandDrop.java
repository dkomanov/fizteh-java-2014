package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;

import java.io.IOException;
import java.io.PrintStream;

public class CommandDrop<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandDrop() {
        super("drop", "drop <table name>");
    }

    public final void executeCommand(final String params,
                                     final TableOperations ops,
                                     PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }

        try {
            ops.dropTable(parameters[0]);
            out.println("dropped");
        } catch (IllegalStateException e) {
            out.println(e.getMessage());
        } catch (IOException e) {
            out.println(e.getMessage());
        }
    }
}
