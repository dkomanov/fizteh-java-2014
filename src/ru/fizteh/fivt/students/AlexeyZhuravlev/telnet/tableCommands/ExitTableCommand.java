package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ExitCommandException;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class ExitTableCommand extends TableCommand {

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        throw new ExitCommandException();
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
