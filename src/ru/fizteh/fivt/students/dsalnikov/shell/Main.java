package ru.fizteh.fivt.students.dsalnikov.shell;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.*;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        ShellState s = new ShellState();
        Shell<ShellState> shell = new Shell<>(s, new StringParser());

        ArrayList<Command> commands = new ArrayList<>();
        commands.add(new RmCommand(shell));
        commands.add(new CpCommand(shell));
        commands.add(new LsCommand(shell));
        commands.add(new ExitCommand(shell));
        commands.add(new MvCommand(shell));
        commands.add(new CdCommand(shell));
        commands.add(new PwdCommand(shell));
        commands.add(new MkdirCommand(shell));
        CatCommand cat = new CatCommand(shell);
        commands.add(cat);
        shell.setCommands(commands);

        if (args.length == 0) {
            try {
                shell.interactiveMode();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            try {
                shell.batchMode(args);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

