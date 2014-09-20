package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.Utils.ShellState;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.IOException;


public class LsCommand implements Command {
    public String getName() {
        return "ls";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(String[] s) throws IOException {
        if (s.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of Command ls: wrong amount of arguments");
        } else {
            ShellState sh = link.getState();
            File dir = new File(sh.getState());
            File[] arr = dir.listFiles();
            for (File f : arr) {
                    System.out.println(f.getName());
            }
        }
    }

    public LsCommand(Shell s) {
        link = s;
    }

    private Shell link;

}
