package ru.fizteh.fivt.students.Bulat_Galiev.storeable.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.TabledbProvider;

public class CommandCommit implements Command {
    public final String getName() {
        return "commit";
    }

    public final int getArgumentsCount() {
        return 0;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        Table curTable = ((TabledbProvider) provider).getDataBase();
        if (curTable != null) {
            System.out.println(curTable.commit());
        } else {
            System.err.println("no table selected");
        }
    }
}
