package ru.fizteh.fivt.students.Bulat_Galiev.junit.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProvider;

public class CommandPut implements Command {
    public final String getName() {
        return "put";
    }

    public final int getArgumentsCount() {
        return 2;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        Table curTable = ((TabledbProvider) provider).getDataBase();
        if (curTable != null) {
            String putValue = curTable.put(arg[1], arg[2]);
            if (putValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(putValue);
            }
        } else {
            System.err.println("no table selected");
        }
    }
}
