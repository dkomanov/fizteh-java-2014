package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

import java.io.IOException;

/**
 * Created by moskupols on 09.12.14.
 */
public class Drop extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 2;
    }

    @Override
    public String name() {
        return "drop";
    }

    @Override
    protected void performAction(Object context, String[] args)
            throws CommandExecutionException {
        StoreableContext cont = (StoreableContext) context;
        Table curTable = cont.getCurrentTable();
        final TableProvider provider = cont.getProvider();
        final boolean removingCur = curTable != null && curTable.getName().equals(args[1]);
        final boolean existed = removingCur || provider.getTable(args[1]) != null;
        if (existed) {
            System.out.println("dropped");
        } else {
            System.out.println(String.format("%s not exists", args[1]));
        }
        try {
            provider.removeTable(args[1]);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (removingCur) {
            cont.setCurrentTable(null);
        }
    }
}
