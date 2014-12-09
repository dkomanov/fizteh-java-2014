package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;

/**
 * Created by moskupols on 03.12.14.
 */
public class ExitCommand extends KnownArgsCountNameFirstCommand {
    @Override
    public int expectedArgsCount() {
        return 1;
    }

    @Override
    public String name() {
        return "exit";
    }

    @Override
    protected void performAction(Object context, String[] args) throws StopProcessingException {
        throw new StopProcessingException();
    }
}
