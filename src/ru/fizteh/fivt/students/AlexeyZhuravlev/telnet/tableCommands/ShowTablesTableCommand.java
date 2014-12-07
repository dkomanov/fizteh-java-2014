package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class ShowTablesTableCommand extends TableCommand {
    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        for (String name: base.getTableNames()) {
            out.println(name + " " + base.getTable(name).size());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
