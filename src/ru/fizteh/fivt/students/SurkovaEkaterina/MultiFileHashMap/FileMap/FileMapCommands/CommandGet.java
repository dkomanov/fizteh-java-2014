package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandGet<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations> {
    public CommandGet() {
        super("get", "get <key>");
    }

    public final void executeCommand(final String params,
                                     final FileMapShellOperations operations) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("get: Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("get: Needs more parameters!");
        }

        if (operations.getTable() == null) {
<<<<<<< HEAD:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandGet.java
            System.out.println("get: No table!");
=======
            System.out.println("no table");
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandGet.java
            return;
        }

        Key key = operations.parseKey(parameters[0]);
        Value value = operations.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(operations.valueToString(value));
        }
    }
}
