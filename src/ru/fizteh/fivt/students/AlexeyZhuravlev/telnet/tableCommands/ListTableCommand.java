package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;
import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class ListTableCommand extends TableCommand {
    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        if (base.getUsing() == null) {
            out.println("no table");
        } else {
            List<String> list = base.getUsing().list();
            out.println(String.join(", ", list));
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
