package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap.FileMapShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell.CommandsParser;

public class CommandRollback<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations>  {
    public CommandRollback() {
        super("rollback", "rollback");
    }

    public void executeCommand(final String params,
                               final FileMapShellOperations operations) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (operations.getTable() == null) {
            System.out.println(this.getClass().toString() + ": No table!");
            return;
        }

        System.out.println(operations.rollback());
    }
}
