package ru.fizteh.fivt.students.Bulat_Galiev.junit.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProvider;

public class CommandGet implements Command {
    public final String getName() {
        return "get";
    }

    public final int getArgumentsCount() {
        return 1;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        Table curTable = ((TabledbProvider) provider).getDataBase();
        if (curTable != null) {
            String getValue = curTable.get(arg[1]);
            if (getValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(getValue);
            }
        } else {
            System.err.println("no table selected");
        }
    }
}
