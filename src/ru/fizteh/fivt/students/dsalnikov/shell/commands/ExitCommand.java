package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.InputStream;
import java.io.PrintStream;

public class ExitCommand extends AbstractCommand {

    private Shell link;

    public ExitCommand(Shell s) {
        super("exit", 0);
        link = s;
    }

    public void execute(String[] st, InputStream inputStream, PrintStream outputStream) {
        System.exit(0);
    }
}
