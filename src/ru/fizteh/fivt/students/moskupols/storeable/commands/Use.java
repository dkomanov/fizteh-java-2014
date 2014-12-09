package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.storeable.KnownDiffStructuredTable;
import ru.fizteh.fivt.students.moskupols.storeable.KnownDiffStructuredTableProvider;

/**
 * Created by moskupols on 09.12.14.
 */
public class Use extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 2;
    }

    @Override
    public String name() {
        return "use";
    }

    @Override
    protected void performAction(Object context, String[] args) throws CommandExecutionException {
        final StoreableContext cont = (StoreableContext) context;
        final KnownDiffStructuredTable currentTable = cont.getCurrentTable();
        final KnownDiffStructuredTableProvider provider = cont.getProvider();
        if (currentTable != null) {
            if (currentTable.diff() != 0) {
                System.out.printf("%d uncommitted changes\n", currentTable.diff());
            }
        }
        KnownDiffStructuredTable newTable = provider.getTable(args[1]);
        if (newTable == null) {
            System.out.println(String.format("%s not exists", args[1]));
        } else {
            System.out.println(String.format("using %s", args[1]));
            cont.setCurrentTable(newTable);
        }

    }
}
