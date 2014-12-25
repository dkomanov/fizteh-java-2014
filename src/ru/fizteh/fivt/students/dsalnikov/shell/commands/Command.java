package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import java.io.InputStream;
import java.io.PrintStream;

public interface Command {
    void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception;

    String getName();

    int getArgsCount();
}
