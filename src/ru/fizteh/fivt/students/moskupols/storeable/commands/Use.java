package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

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
        final Table currentTable = cont.getCurrentTable();
        final TableProvider provider = cont.getProvider();
        if (currentTable != null) {
            if (currentTable.getNumberOfUncommittedChanges() != 0) {
                throw new CommandExecutionException(
                        this,
                        String.format(
                                "%d uncommitted changes\n", currentTable.getNumberOfUncommittedChanges()));
            }
        }
        Table newTable = provider.getTable(args[1]);
        if (newTable == null) {
            throw new CommandExecutionException(this, String.format("%s not exists", args[1]));
        } else {
            System.out.println(String.format("using %s", args[1]));
            cont.setCurrentTable(newTable);
        }

    }
}
