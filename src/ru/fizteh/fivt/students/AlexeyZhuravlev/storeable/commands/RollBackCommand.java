package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class RollBackCommand extends Command {

    @Override
    public void execute(StructuredTableProvider base) throws Exception {

    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
