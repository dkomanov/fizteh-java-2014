package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapShellOperationsInterface;

public class CommandRemove<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations> {
    public CommandRemove() {
        super("remove", "remove <key>");
    }

    public final void executeCommand(final String params,
                                     final FileMapShellOperations operations) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 1) {
<<<<<<< HEAD:src/ru/fizteh/fivt/students/SurkovaEkaterina/Parallel/FileMap/FileMapCommands/CommandRemove.java
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }

        if (operations.getTable() == null) {
            System.out.println(this.getClass().toString() + ": No table!");
=======
            throw new IllegalArgumentException("remove: Not enough arguments!");
        }

        if (operations.getTable() == null) {
            System.out.println("remove: No table!");
>>>>>>> 9e55f5a09a2dfc3fdca591ae3651d9e6d4270c41:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandRemove.java
            return;
        }

        Key key = operations.parseKey(parameters[0]);
        Value value = operations.remove(key);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
