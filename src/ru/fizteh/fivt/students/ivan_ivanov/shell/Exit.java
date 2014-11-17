package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.IOException;

public class Exit implements Command {

    @Override
    public final String getName() {

        return "exit";
    }

    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {

        System.exit(0);
    }
}
