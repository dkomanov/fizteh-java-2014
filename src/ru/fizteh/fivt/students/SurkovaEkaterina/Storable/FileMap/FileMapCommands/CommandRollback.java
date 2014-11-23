package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.FileMap.FileMapShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell.CommandsParser;

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
            throw new IllegalArgumentException("rollback: Too many arguments!");
        }
        if (operations.getTable() == null) {
            System.out.println("rollback: No table!");
            return;
        }

        System.out.println(operations.rollback());
    }
}
