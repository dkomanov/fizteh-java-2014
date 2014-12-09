package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.InputStream;
import java.io.PrintStream;

public class ExitCommand implements Command {

    private Shell link;

    public ExitCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "exit";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(String[] st, InputStream inputStream, PrintStream outputStream) {
        System.exit(0);
    }
}
