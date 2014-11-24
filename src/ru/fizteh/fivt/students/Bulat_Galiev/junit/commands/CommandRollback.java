package ru.fizteh.fivt.students.Bulat_Galiev.junit.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProvider;

public class CommandRollback implements Command {
    public final String getName() {
        return "rollback";
    }

    public final int getArgumentsCount() {
        return 0;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        Table curTable = ((TabledbProvider) provider).getDataBase();
        if (curTable != null) {
            curTable.rollback();
        } else {
            System.err.println("no table selected");
        }
    }
}
