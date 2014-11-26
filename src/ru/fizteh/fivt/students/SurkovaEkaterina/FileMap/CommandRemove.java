package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.shell.CommandsParser;

public class CommandRemove<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations> {
    public CommandRemove() {
        super("remove", "remove <key>");
    }

    public final void executeCommand(final String params,
                                     final FileMapShellOperations operations) {
        if (operations.getTable() == null) {
            System.err.println("remove: Table unavailable!");
            return;
        }

        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("remove: Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("remove: not enough arguments!");
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
