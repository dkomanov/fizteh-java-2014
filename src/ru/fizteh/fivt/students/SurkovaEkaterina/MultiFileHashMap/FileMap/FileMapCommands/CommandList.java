package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperationsInterface;

import java.util.List;

public class CommandList<FileMapShellOperations
        extends FileMapShellOperationsInterface>
        extends ACommand<FileMapShellOperations> {
    public CommandList() {
        super("list", "list");
    }

    public final void executeCommand(final String parameters,
                                     final FileMapShellOperations operations) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("list: Needs no parameters!");
        }

        if (operations.getTable() == null) {
<<<<<<< HEAD:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandList.java
            System.out.println("list: No table!");
=======
            System.out.println("no table");
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4:src/ru/fizteh/fivt/students/SurkovaEkaterina/MultiFileHashMap/FileMap/FileMapCommands/CommandList.java
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
