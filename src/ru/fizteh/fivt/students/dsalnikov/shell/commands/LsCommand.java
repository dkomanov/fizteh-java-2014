package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.File;
import java.io.IOException;


public class LsCommand implements Command {

    private Shell link;

    public LsCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "ls";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(String[] s) throws IOException {
        ShellState sh = link.getState();
        File dir = new File(sh.getState());
        File[] arr = dir.listFiles();
        for (File f : arr) {
            System.out.println(f.getName());
        }
    }
}
