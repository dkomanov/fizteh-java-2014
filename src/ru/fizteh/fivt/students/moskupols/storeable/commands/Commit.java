package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.commands.KnownArgsCountNameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.storeable.KnownDiffStructuredTable;

import java.io.IOException;

/**
 * Created by moskupols on 09.12.14.
 */
public class Commit extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 1;
    }

    @Override
    public String name() {
        return "commit";
    }

    @Override
    protected void performAction(Object context, String[] args) throws CommandExecutionException {
        final KnownDiffStructuredTable currentTable = ((StoreableContext) context).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        try {
            System.out.println(currentTable.commit());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
