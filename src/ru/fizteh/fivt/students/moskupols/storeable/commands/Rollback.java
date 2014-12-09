package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 09.12.14.
 */
public class Rollback extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 1;
    }

    @Override
    public String name() {
        return "rollback";
    }

    @Override
    protected void performAction(Object context, String[] args) throws CommandExecutionException {
        final Table currentTable = ((StoreableContext) context).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        System.out.println(currentTable.rollback());
    }
}
