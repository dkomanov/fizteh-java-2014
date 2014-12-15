package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.Command;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands.*;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands.*;

import java.util.ArrayList;
import java.util.List;

public class MultifileMapMain {
    public static void main(String[] Args) {
        Shell<MultifileMapShellState> shell = new Shell<MultifileMapShellState>();

        List<Command<?>> commands = new ArrayList<Command<?>>();

        Command<FileMapShellState> command = new PutCommand();
        commands.add(command);

        command = new GetCommand();
        commands.add(command);

        command = new RemoveCommand();
        commands.add(command);

        command = new CommitCommand();
        commands.add(command);

        command = new RollbackCommand();
        commands.add(command);

        command = new ListCommand();
        commands.add(command);

        command = new ExitCommand();
        commands.add(command);

        Command<MultifileMapShellState> command1 = new CreateCommand();
        commands.add(command1);

        command1 = new DropCommand();
        commands.add(command1);

        command1 = new UseCommand();
        commands.add(command1);

        command1 = new ShowCommand();
        commands.add(command1);

        command = new HelpCommand<FileMapShellState>(commands);
        commands.add(command);

        shell.setCommands(commands);

        try {
            String databaseDirectory = System.getProperty("fizteh.db.dir");
            MultifileMapShellState shellState = new MultifileMapShellState();
            DatabaseFactory factory = new DatabaseFactory();
            shellState.tableProvider = (DatabaseTableProvider)factory.create(databaseDirectory);
            shell.setShellState(shellState);
        } catch (IllegalArgumentException e) {
            System.err.println("error: " + e.getMessage());
            System.exit(-1);
        }

        shell.start();
    }
}
