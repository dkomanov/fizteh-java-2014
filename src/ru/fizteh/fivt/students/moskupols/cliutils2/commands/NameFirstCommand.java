package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.InvalidArgsException;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class NameFirstCommand extends AbstractCommand {

    @Override
    public void checkArgs(String[] args) throws InvalidArgsException {
        if (!name().equals(args[0])) {
            throw new InvalidArgsException(this, name() + "should be the first argument");
        }
    }
}
