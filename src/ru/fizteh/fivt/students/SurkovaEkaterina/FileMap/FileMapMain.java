package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.shell.Command;
import ru.fizteh.fivt.students.SurkovaEkaterina.shell.Shell;

import java.util.ArrayList;

public class FileMapMain {
    public static void main(final String[] args) {
        Shell<FileMapShellOperations> shell
                = new Shell<FileMapShellOperations>();

        ArrayList<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(new CommandPut());
        commands.add(new CommandGet());
        commands.add(new CommandRemove());
        commands.add(new CommandList());
        commands.add(new CommandExit());

        shell.setShellCommands(commands);

        FileMapShellOperations shellState = new FileMapShellOperations();
        String databaseDirectory = System.getProperty("fizteh.db.dir");
        shellState.table = new FileTable(databaseDirectory, "master");
        shell.setShellOperations(shellState);
        shell.beginExecuting();
    }
}
