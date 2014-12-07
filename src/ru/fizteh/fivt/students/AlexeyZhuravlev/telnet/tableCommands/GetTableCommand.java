package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class GetTableCommand extends TableCommand {

    String key;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        if (base.getUsing() == null) {
            out.println("no table");
        } else {
            Storeable value = base.getUsing().get(key);
            if (value == null) {
                out.println("not found");
            } else {
                out.println("found");
                out.println(base.serialize(base.getUsing(), value));
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    public void putArguments(String[] args) {
        key = args[1];
    }
}
