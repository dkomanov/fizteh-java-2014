package ru.fizteh.fivt.students.dsalnikov.shell;

import ru.fizteh.fivt.students.dsalnikov.shell.Commands.*;
import ru.fizteh.fivt.students.dsalnikov.Utils.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        ShellState s = new ShellState();
        Shell<ShellState> shell = new Shell<ShellState>(s);

        ArrayList<Command> Commands = new ArrayList();
        RmCommand rm = new RmCommand(shell);
        Commands.add(rm);
        CpCommand cp = new CpCommand(shell);
        Commands.add(cp);
        LsCommand dir = new LsCommand(shell);
        Commands.add(dir);
        ExitCommand exit = new ExitCommand(shell);
        Commands.add(exit);
        MvCommand mv = new MvCommand(shell);
        Commands.add(mv);
        CdCommand cd = new CdCommand(shell);
        Commands.add(cd);
        PwdCommand pwd = new PwdCommand(shell);
        Commands.add(pwd);
        MkdirCommand mkdir = new MkdirCommand(shell);
        Commands.add(mkdir);
        CatCommand cat = new CatCommand(shell);
        Commands.add(cat);
        shell.setCommands(Commands);

        if (args.length == 0) {
            try {
                shell.batchMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                shell.commandMode(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

