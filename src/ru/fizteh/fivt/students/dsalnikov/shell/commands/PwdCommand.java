package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.InputStream;
import java.io.PrintStream;

public class PwdCommand implements Command {

    private Shell link;

    public PwdCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "pwd";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(String[] emptyStr, InputStream inputStream, PrintStream outputStream) {
        System.out.println(link.getState().getState());
    }
}
