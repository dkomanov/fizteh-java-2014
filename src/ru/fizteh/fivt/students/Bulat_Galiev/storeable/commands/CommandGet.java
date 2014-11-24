package ru.fizteh.fivt.students.Bulat_Galiev.storeable.commands;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.storeable.TabledbProvider;

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
            ((TabledbProvider) provider).checkTable(curTable);
            Storeable getValue = ((Tabledb) curTable).get(arg[1]);
            String stringValue = provider.serialize(curTable, getValue);
            if (getValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(stringValue);
            }
        } else {
            System.err.println("no table selected");
        }
    }
}
