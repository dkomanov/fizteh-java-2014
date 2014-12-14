package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapShellOperationsInterface;

public class CommandPut<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations> {
    public CommandPut() {
        super("put", "put <key> <value>");
    }

    public final void executeCommand(final String params,
                                     final FileMapShellOperations operations) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 2) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 2) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }

        if (operations.getTable() == null) {
<<<<<<< HEAD:src/ru/fizteh/fivt/students/SurkovaEkaterina/Parallel/FileMap/FileMapCommands/CommandPut.java
            System.out.println(this.getClass().toString() + ": No table!");
=======
            System.out.println("put: No table!");
>>>>>>> 9e55f5a09a2dfc3fdca591ae3651d9e6d4270c41:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandPut.java
            return;
        }

        Key key = operations.parseKey(parameters[0]);
        Value value = operations.parseValue(parameters[1]);
        Value oldValue = operations.put(key, value);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(operations.valueToString(oldValue));
        }
    }
}
