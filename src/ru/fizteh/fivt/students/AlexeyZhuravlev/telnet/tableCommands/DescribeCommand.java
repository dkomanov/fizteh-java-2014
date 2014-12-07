package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.TypeTransformer;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class DescribeCommand extends TableCommand {

    String name;

    @Override
    public void execute(ShellTableProvider base, PrintStream out) throws Exception {
        Table table = base.getTable(name);
        if (table == null) {
            out.println(name + " not found");
        } else {
            List<Class<?>> types = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); i++) {
                types.add(table.getColumnType(i));
            }
            String result = TypeTransformer.stringFromTypeList(types);
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
