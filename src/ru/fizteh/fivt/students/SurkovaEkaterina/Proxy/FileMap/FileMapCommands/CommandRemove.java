package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.FileMap.FileMapShellOperationsInterface;

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
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }

        if (operations.getTable() == null) {
            System.out.println(this.getClass().toString() + ": No table!");
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
