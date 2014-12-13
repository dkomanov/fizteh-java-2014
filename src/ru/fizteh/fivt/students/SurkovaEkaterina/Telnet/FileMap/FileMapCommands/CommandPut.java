package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.FileMapShellOperationsInterface;

import java.io.PrintStream;

public class CommandPut<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations> {
    public CommandPut() {
        super("put", "put <key> <value>");
    }

    public final void executeCommand(final String params,
                                     final FileMapShellOperations operations, PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 2) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (parameters.length < 2) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Not enough arguments!");
        }

        if (operations.getTable() == null) {
            System.out.println(this.getClass().toString() + ": No table!");
            return;
        }

        Key key = operations.parseKey(parameters[0]);
        Value value = operations.parseValue(parameters[1]);
        Value oldValue = operations.put(key, value);

        if (oldValue == null) {
            out.println("new");
        } else {
            out.println("overwrite");
            out.println(operations.valueToString(oldValue));
        }
    }
}
