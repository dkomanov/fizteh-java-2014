package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


public class LsCommand extends AbstractCommand {

    private Shell link;

    public LsCommand(Shell s) {
        super("ls", 0);
        link = s;
    }

    public void execute(String[] s, InputStream inputStream, PrintStream outputStream) throws IOException {
        ShellState sh = link.getState();
        File dir = new File(sh.getState());
        File[] arr = dir.listFiles();
        for (File f : arr) {
            outputStream.println(f.getName());
        }
    }
}
