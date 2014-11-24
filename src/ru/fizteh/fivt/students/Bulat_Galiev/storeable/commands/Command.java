package ru.fizteh.fivt.students.Bulat_Galiev.storeable.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;

public interface Command {
    String getName();

    int getArgumentsCount();

    void execute(final TableProvider provider, final String[] args)
            throws IOException;
}
