package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.*;

import java.io.IOException;

import java.util.ArrayList;

public class Program {
    public static void main(String[] args) throws IOException {
        Shell shell = new Shell();

        ArrayList<Command<?>> commands = new ArrayList<Command<?>>();

        Command<FileSystemShellState> command = new MakeDirCommand();
        commands.add(command);

        command = new DirCommand();
        commands.add(command);

        command = new CdCommand();
        commands.add(command);

        command = new PwdCommand();
        commands.add(command);

        command = new RmCommand();
        commands.add(command);

        command = new MvCommand();
        commands.add(command);

        command = new ExitCommand();
        commands.add(command);

        command = new CopyCommand();
        commands.add(command);

        command = new CatCommand();
        commands.add(command);

        command = new HelpCommand<FileSystemShellState>(commands);
        commands.add(command);

        shell.setCommands(commands);
        shell.setShellState(new FileSystemShellState());
        shell.setArgs(args);
        shell.start();
    }
}
