package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import java.io.PrintStream;

public interface Command {
    String getName();

    int getArgsNum();

    void exec(String[] args, PrintStream out);
}
