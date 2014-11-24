package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

public class ShowTablesCommand extends Command {
    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        for (String name: base.getTableNames()) {
            System.out.println(name + " " + base.getTable(name).size());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
