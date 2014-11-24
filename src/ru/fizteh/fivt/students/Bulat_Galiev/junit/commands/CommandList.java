package ru.fizteh.fivt.students.Bulat_Galiev.junit.commands;

import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProvider;

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
            List<String> list = curTable.list();
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
