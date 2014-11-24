package ru.fizteh.fivt.students.Bulat_Galiev.junit.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProvider;

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
            int diff = ((Tabledb) ((TabledbProvider) provider)
                    .getDataBase()).getChangedRecordsNumber();
            if (diff == 0) {
                System.exit(0);
            } else {
                System.err
                        .println(diff
                                + " unsaved changes. "
                                + "Do commit or rollback command before exit");
            }
        } else {
            System.exit(0);
        }
    }
}
