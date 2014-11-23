package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.CommandsParser;

public class CommandCommit<TableOperations extends DatabaseShellOperationsInterface> extends ACommand<TableOperations> {
    public CommandCommit() {
        super("commit", "commit");
    }

    public void executeCommand(String params, TableOperations operations) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("commit: Too many arguments!");
        }
        if (operations.getTable() == null) {
            System.err.println("No table!");
            return;
        }
        System.out.println(operations.commit());
    }
}
