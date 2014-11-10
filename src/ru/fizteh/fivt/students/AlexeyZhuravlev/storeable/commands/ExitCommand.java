package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;


import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ExitCommandException;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class ExitCommand extends Command {

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        throw new ExitCommandException();
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
