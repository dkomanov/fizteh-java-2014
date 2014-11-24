package ru.fizteh.fivt.students.Bulat_Galiev.storeable.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;

public class CommandDrop implements Command {
    public final String getName() {
        return "drop";
    }

    public final int getArgumentsCount() {
        return 1;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        provider.removeTable(arg[1]);
    }
}
