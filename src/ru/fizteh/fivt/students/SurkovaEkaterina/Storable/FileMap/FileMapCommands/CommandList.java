package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell.ACommand;

import java.util.List;

public class CommandList<TableOperations extends DatabaseShellOperationsInterface> extends ACommand<TableOperations> {
    public CommandList() {
        super("list", "list");
    }

    public void executeCommand(String params, TableOperations operations) {
        if (!params.isEmpty()) {
            throw new IllegalArgumentException("list: Needs no parameters!");
        }

        if (operations.getTable() == null) {
            System.out.println("No table!");
            return;
        }

        List<String> list = operations.list();
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (!s.equals(list.toArray()[list.toArray().length - 1])) {
                sb.append(s + ", ");
            } else {
                sb.append(s);
            }
        }
        System.out.println(sb.toString());
    }
}
