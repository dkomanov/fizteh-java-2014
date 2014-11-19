package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;

import java.io.PrintStream;

public interface Command {
    String getName();

    int getArgsNum();

    void exec(String[] args, PrintStream out);
}
