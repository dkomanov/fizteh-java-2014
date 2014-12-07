package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapCommands.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.Command;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.Shell;

import java.util.ArrayList;

public class JUnitMain {
    public static void main(final String[] args) {
        Shell<MultiFileHashMapOperations> shell =
                new Shell<MultiFileHashMapOperations>();

        ArrayList<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(new CommandPut());
        commands.add(new CommandGet());
        commands.add(new CommandRemove());
        commands.add(new CommandList());
        commands.add(new CommandExit());
        commands.add(new CommandCreate());
        commands.add(new CommandDrop());
        commands.add(new CommandUse());
        commands.add(new CommandShowTables());
        commands.add(new CommandSize());
        commands.add(new CommandCommit());
        commands.add(new CommandRollback());

        shell.setShellCommands(commands);
        shell.setArguments(args);

        try {
            String databaseDirectory =
                    System.getProperty("fizteh.db.dir");

            MultiFileHashMapOperations operations =
                    new MultiFileHashMapOperations();

            MultiFileHashMapTableProviderFactory factory =
                    new MultiFileHashMapTableProviderFactory();

            operations.tableProvider = factory.create(databaseDirectory);

            shell.setShellOperations(operations);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        shell.beginExecuting();
    }
}
