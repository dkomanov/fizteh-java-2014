package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class ShowTablesCommand extends Command {
    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        for (String name: base.getTableNames()) {
            System.out.println(name + " " + base.getTable(name).size());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
