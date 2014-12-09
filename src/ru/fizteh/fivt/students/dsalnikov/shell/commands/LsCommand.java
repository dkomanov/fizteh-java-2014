package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


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

    public void execute(String[] s, InputStream inputStream, PrintStream outputStream) throws IOException {
        ShellState sh = link.getState();
        File dir = new File(sh.getState());
        File[] arr = dir.listFiles();
        for (File f : arr) {
            outputStream.println(f.getName());
        }
    }
}
