package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.InputStream;
import java.io.PrintStream;

public class PwdCommand extends AbstractCommand {

    private Shell link;

    public PwdCommand(Shell s) {
        super("pwd", 0);
        link = s;
    }

    public void execute(String[] emptyStr, InputStream inputStream, PrintStream outputStream) {
        System.out.println(link.getState().getState());
    }
}
