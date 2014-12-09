package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 09.12.14.
 */
public class Get extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 2;
    }

    @Override
    public String name() {
        return "get";
    }

    @Override
    protected void performAction(Object context, String[] args)
            throws CommandExecutionException, StopProcessingException {
        final StoreableContext cont = (StoreableContext) context;
        final Table table = cont.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            return;
        }
        Storeable ret = table.get(args[1]);
        String str;
        if (ret == null) {
            str = "not found";
        } else {
            str = cont.getProvider().serialize(table, ret);
        }
        System.out.println(str);
    }
}
