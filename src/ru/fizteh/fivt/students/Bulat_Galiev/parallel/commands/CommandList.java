package ru.fizteh.fivt.students.Bulat_Galiev.parallel.commands;

import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.Tabledb;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.TabledbProvider;

public class CommandList implements Command {
    public final String getName() {
        return "list";
    }

    public final int getArgumentsCount() {
        return 0;
    }

    public final void execute(final TableProvider provider, final String[] arg)
            throws IOException {
        Table curTable = ((TabledbProvider) provider).getDataBase();
        if (curTable != null) {
            ((TabledbProvider) provider).checkTable(curTable);
            List<String> list = ((Tabledb) curTable).list();
            int iteration = 0;
            for (String current : list) {
                iteration++;
                System.out.print(current);
                if (iteration != list.size()) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        } else {
            System.err.println("no table selected");
        }
    }
}
