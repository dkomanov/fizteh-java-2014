package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.CommandsParser;

public class CommandSize<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations>  {
    public CommandSize() {
        super("size", "size");
    }

    public void executeCommand(String params, FileMapShellOperations shellState) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many parameters!");
        }

        int size = shellState.size();
        System.out.println(size);
    }
}
