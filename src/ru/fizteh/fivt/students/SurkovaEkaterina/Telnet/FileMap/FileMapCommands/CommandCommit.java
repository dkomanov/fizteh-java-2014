package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;

import java.io.PrintStream;

public class CommandCommit<TableOperations extends DatabaseShellOperationsInterface> extends ACommand<TableOperations> {
    public CommandCommit() {
        super("commit", "commit");
    }

    public void executeCommand(String params, TableOperations operations, PrintStream out) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Too many arguments!");
        }
        if (operations.getTable() == null) {
            out.println(this.getClass().toString() + ": No table!");
            return;
        }
        out.println(operations.commit());
    }
}
