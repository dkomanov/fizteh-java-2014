package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;
import java.text.ParseException;

/**
 * @author AlexeyZhuravlev
 */
public class PutTableCommand extends TableCommand {

    String key;
    String value;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            try {
                Storeable storeable = base.deserialize(base.getUsing(), value);
                Storeable old = base.getUsing().put(key, storeable);
                if (old == null) {
                    out.println("new");
                } else {
                    out.println("overwrite");
                    out.println(base.serialize(base.getUsing(), old));
                }
            } catch (ParseException e) {
                throw new Exception("wrong type (" + e.getMessage() + ")");
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }
}
