package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap.FileMapShellOperationsInterface;

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
            System.out.println("put: No table!");
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
