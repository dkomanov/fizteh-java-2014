package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientExit implements Command {
    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        Thread.currentThread().interrupt();
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
