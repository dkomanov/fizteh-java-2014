package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.CommandsParser;

import java.io.IOException;

public class CommandDrop<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandDrop() {
        super("drop", "drop <table name>");
    }

    public final void executeCommand(final String params,
                                     final TableOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }

        try {
            ops.dropTable(parameters[0]);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
