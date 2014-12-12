package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.InvalidArgsException;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class KnownArgsCountNameFirstCommand extends NameFirstCommand {
    public abstract int expectedArgsCount();

    @Override
    public void checkArgs(String[] args) throws InvalidArgsException {
        super.checkArgs(args);
        if (args.length != expectedArgsCount()) {
            throw new InvalidArgsException(this,
                    String.format("%d arguments expected", expectedArgsCount()));
        }
    }
}
