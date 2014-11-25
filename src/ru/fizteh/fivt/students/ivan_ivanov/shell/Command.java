package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.IOException;

public interface Command {

    String getName();

    void executeCmd(Shell shell, String[] args) throws IOException;
}
