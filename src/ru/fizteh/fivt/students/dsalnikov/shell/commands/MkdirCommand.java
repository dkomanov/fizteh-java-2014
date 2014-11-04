package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class MkdirCommand implements Command {

    private Shell link;

    public MkdirCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "mkdir";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(String[] s) throws IOException {
        if (s.length != 2) {
            throw new IllegalArgumentException("Incorrect usage of Command mkdir: wrong amount of arguments");
        } else {
            ShellState sh = link.getState();
            File f = new File(s[1]);
            if (!f.isAbsolute()) {
                f = new File(sh.getState(), s[1]);
            }
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Creating directory failed");
                }
            } else {
                throw new FileAlreadyExistsException("Directory already exists:" + f.getName());
            }
        }
    }
}

