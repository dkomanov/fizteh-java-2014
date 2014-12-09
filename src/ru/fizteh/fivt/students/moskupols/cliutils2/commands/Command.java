package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.InvalidArgsException;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class Command {
    public abstract String name();
    public abstract String checkArgs(String[] args);
    protected abstract void performAction(Object context, String[] args)
            throws CommandExecutionException, StopProcessingException;

    public final void perform(Object context, String[] args)
            throws CommandExecutionException, StopProcessingException {
        String argsError = checkArgs(args);
        if (argsError != null) {
            throw new InvalidArgsException(this, argsError);
        }

        performAction(context, args);
    }
}
