package ru.fizteh.fivt.students.dsalnikov.shell;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.*;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        ShellState s = new ShellState();
        Shell<ShellState> shell = new Shell<>(s);

        ArrayList<Command> commands = new ArrayList<>();
        RmCommand rm = new RmCommand(shell);
        commands.add(rm);
        CpCommand cp = new CpCommand(shell);
        commands.add(cp);
        LsCommand dir = new LsCommand(shell);
        commands.add(dir);
        ExitCommand exit = new ExitCommand(shell);
        commands.add(exit);
        MvCommand mv = new MvCommand(shell);
        commands.add(mv);
        CdCommand cd = new CdCommand(shell);
        commands.add(cd);
        PwdCommand pwd = new PwdCommand(shell);
        commands.add(pwd);
        MkdirCommand mkdir = new MkdirCommand(shell);
        commands.add(mkdir);
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

