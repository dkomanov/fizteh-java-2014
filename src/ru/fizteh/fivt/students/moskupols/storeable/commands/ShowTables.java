package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.FixedArgsNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by moskupols on 09.12.14.
 */
public class ShowTables extends FixedArgsNameFirstCommand {
    @Override
    public String[] expectedArgs() {
        return new String[] {"show", "tables"};
    }

    @Override
    public String name() {
        return "show";
    }

    @Override
    protected void performAction(Object context, String[] args) throws CommandExecutionException {
        final StoreableContext cont = (StoreableContext) context;
        final TableProvider provider = cont.getProvider();
        final Table currentTable = cont.getCurrentTable();
        List<String> lines = new LinkedList<>();
        for (String name : provider.getTableNames()) {
            final Table table =
                    currentTable != null && name.equals(currentTable.getName())
                            ? currentTable
                            : provider.getTable(name);
            lines.add(String.format("%s %d", name, table.size()));
        }
        lines.forEach(System.out::println);

    }
}
