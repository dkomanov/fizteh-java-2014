package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.TypeTransformer;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class DescribeCommand extends TableCommand {

    String name;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        AdvancedTable table = (AdvancedTable) base.getTable(name);
        if (table == null) {
            out.println(name + " not found");
        } else {
            String result = TypeTransformer.stringFromTypeList(table.getStructuredTable().getTypes());
            out.println(result);
        }
    }

    @Override
    protected void putArguments(String[] args) {
        name = args[1];
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }
}
