package ru.fizteh.fivt.students.Bulat_Galiev.junit.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface Command {
    String getName();

    int getArgumentsCount();

    void execute(final TableProvider provider, final String[] args)
            throws IOException;
}
