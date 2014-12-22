package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.InvalidArgsException;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class AbstractCommand implements Command {
    public abstract void checkArgs(String[] args) throws InvalidArgsException;
    protected abstract void performAction(Object context, String[] args)
            throws CommandExecutionException;

    @Override
    public final void perform(Object context, String[] args)
            throws CommandExecutionException {
        checkArgs(args);
        performAction(context, args);
    }
}
