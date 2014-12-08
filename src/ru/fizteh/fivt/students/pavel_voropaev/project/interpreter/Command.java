package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import java.io.IOException;

public interface Command {
    String getName();

    int getArgsNum();

    void exec(String[] args) throws IOException;
}
