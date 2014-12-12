package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 09.12.14.
 */
public class Remove extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 2;
    }

    @Override
    public String name() {
        return "remove";
    }

    @Override
    protected void performAction(Object context, String[] args) throws CommandExecutionException {
        final Table currentTable = ((StoreableContext) context).getCurrentTable();
        if (currentTable == null) {
            throw new CommandExecutionException(this, "no table");
        }
        if (currentTable.remove(args[1]) == null) {
            throw new CommandExecutionException(this, "not found");
        } else {
            System.out.println("removed");
        }
    }
}
