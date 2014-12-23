package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;

import java.io.PrintStream;
import java.util.List;

public class CommandList<TableOperations
        extends DatabaseShellOperationsInterface> extends ACommand<TableOperations> {
    public CommandList() {
        super("list", "list");
    }

    public void executeCommand(String params, TableOperations operations, PrintStream out) {
        if (!params.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Needs no parameters!");
        }

        if (operations.getTable() == null) {
            out.println(this.getClass().getSimpleName() + ": No table!");
            return;
        }

        List<String> list = operations.list();
        out.println(String.join(", ", list));
    }
}
