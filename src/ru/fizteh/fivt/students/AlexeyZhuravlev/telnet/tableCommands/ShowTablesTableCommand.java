package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.UnknownCommandException;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class ShowTablesTableCommand extends TableCommand {
    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        out.println(base.getTableNames().size());
        for (String name: base.getTableNames()) {
            out.println(name + " " + base.getTable(name).size());
        }
    }

    @Override
    protected void putArguments(String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new UnknownCommandException();
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }
}
