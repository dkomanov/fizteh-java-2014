package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class UseTableCommand extends TableCommand {

    String name;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        if (base.getUsing() != null && base.getUsing().getNumberOfUncommittedChanges() != 0) {
            out.println(base.getUsing().getNumberOfUncommittedChanges() + " unsaved changes");
        } else if (base.use(name) == null) {
            out.println(name + " not exists");
        } else {
            out.println("using " + name);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        name = args[1];
    }
}
