package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class RemoveTableCommand extends TableCommand {

    String key;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable result = base.getUsing().remove(key);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
                System.out.println(base.serialize(base.getUsing(), result));
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
