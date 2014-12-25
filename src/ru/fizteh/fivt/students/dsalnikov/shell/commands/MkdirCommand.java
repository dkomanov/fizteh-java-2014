package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;

public class MkdirCommand extends AbstractCommand {

    private Shell link;

    public MkdirCommand(Shell s) {
        super("mkdir", 1);
        link = s;
    }

    public void execute(String[] s, InputStream inputStream, PrintStream outputStream) throws IOException {
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

