package ru.fizteh.fivt.students.Bulat_Galiev.parallel.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.TabledbProvider;

public class CommandCreate implements Command {
    public final String getName() {
        return "create";
    }

    public final int getArgumentsCount() {
        return 2;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        ((TabledbProvider) provider).createStoreableTable(arg);
    }
}
