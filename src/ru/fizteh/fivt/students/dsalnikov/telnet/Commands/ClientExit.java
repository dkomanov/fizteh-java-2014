package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientExit extends AbstractCommand {
    public ClientExit() {
        super("exit", 0);
    }

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
