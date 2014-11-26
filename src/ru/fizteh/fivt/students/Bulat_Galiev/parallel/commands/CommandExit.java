package ru.fizteh.fivt.students.Bulat_Galiev.parallel.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.TabledbProvider;

public class CommandExit implements Command {
    public final String getName() {
        return "exit";
    }

    public final int getArgumentsCount() {
        return 0;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        Table curTable = ((TabledbProvider) provider).getDataBase();
        if (curTable != null) {
            int diff = ((Tabledb) ((TabledbProvider) provider).getDataBase())
                    .getNumberOfUncommittedChanges();
            if (diff == 0) {
                System.exit(0);
            } else {
                System.err
                        .println(diff
                                + " unsaved changes. Do commit or rollback command before exit");
            }
        } else {
            System.exit(0);
        }
    }
}
