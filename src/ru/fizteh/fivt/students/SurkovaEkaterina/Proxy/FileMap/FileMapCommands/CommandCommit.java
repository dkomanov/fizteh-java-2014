package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell.CommandsParser;

public class CommandCommit<TableOperations extends DatabaseShellOperationsInterface> extends ACommand<TableOperations> {
    public CommandCommit() {
        super("commit", "commit");
    }

    public void executeCommand(String params, TableOperations operations) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (operations.getTable() == null) {
            System.out.println(this.getClass().toString() + ": No table!");
            return;
        }
        System.out.println(operations.commit());
    }
}
