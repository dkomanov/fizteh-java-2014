package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

public class CommitCommand extends Command {

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().commit());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
