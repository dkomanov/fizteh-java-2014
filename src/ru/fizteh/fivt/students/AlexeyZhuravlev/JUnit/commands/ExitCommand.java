package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ExitCommandException;

/**
 * @author AlexeyZhuravlev
 */
public class ExitCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        throw new ExitCommandException();
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
