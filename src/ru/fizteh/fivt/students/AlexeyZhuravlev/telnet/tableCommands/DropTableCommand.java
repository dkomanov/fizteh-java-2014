package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class DropTableCommand extends TableCommand {

    String name;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        try {
            base.removeTable(name);
            out.println("dropped");
        } catch (IllegalStateException e) {
            out.println(name + " not exists");
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
