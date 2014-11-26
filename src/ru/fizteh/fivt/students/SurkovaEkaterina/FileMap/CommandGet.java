package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.shell.CommandsParser;

public class CommandGet<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations> {
    public CommandGet() {
        super("get", "get <key>");
    }

    public final void executeCommand(final String params,
                               final FileMapShellOperations operations) {
        if (operations.getTable() == null) {
            System.err.println("Get: Table unavailable!");
            return;
        }

        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("get: Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("get: Needs more parameters!");
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
