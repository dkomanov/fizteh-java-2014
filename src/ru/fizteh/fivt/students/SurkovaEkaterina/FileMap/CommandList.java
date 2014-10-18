package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.shell.ACommand;

import java.util.ArrayList;
import java.util.List;

public class CommandList<FileMapShellOperations
        extends FileMapShellOperationsInterface>
        extends ACommand<FileMapShellOperations> {
    public CommandList() { super("list", "list"); }

    public final void executeCommand(final String parameters,
                                     final FileMapShellOperations operations) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("list: Needs no parameters!");
        }

        if (operations.getTable() == null) {
            System.err.println("list: Table unavailable!");
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
