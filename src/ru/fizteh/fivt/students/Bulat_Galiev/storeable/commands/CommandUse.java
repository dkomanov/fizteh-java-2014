package ru.fizteh.fivt.students.Bulat_Galiev.storeable.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.TabledbProvider;

public class CommandUse implements Command {
    public final String getName() {
        return "use";
    }

    public final int getArgumentsCount() {
        return 1;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        TabledbProvider.changeCurTable(arg[1]);
    }
}
