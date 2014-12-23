package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;


import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.ExitCommandException;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

public class ExitCommand extends Command {

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        throw new ExitCommandException();
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
