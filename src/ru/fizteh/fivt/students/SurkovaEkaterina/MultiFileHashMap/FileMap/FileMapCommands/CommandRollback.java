package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandRollback extends ACommand<FileMapShellOperations> {
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

        System.out.println(operations.table.rollback());
    }
}
