package ru.fizteh.fivt.students.Bulat_Galiev.parallel.commands;

import java.io.IOException;
import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.TabledbProvider;

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
            ((TabledbProvider) provider).checkTable(curTable);
            Storeable storeableValue;
            try {
                storeableValue = ((Tabledb) curTable).getLocalProvider()
                        .deserialize(curTable, arg[2]);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Deserialising problem: "
                        + e.getMessage());
            }
            Storeable putValue = curTable.put(arg[1], storeableValue);
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
