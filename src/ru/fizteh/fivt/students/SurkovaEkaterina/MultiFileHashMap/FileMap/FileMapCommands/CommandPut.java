package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperationsInterface;

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
            throw new IllegalArgumentException("put: Too many arguments!");
        }
        if (parameters.length < 2) {
            throw new IllegalArgumentException("put: Not enough arguments!");
        }

        if (operations.getTable() == null) {
<<<<<<< HEAD:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandPut.java
            System.out.println("put: No table!");
=======
            System.out.println("no table");
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandPut.java
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
