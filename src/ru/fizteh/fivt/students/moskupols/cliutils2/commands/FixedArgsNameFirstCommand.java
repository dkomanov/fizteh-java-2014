package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.InvalidArgsException;

import java.util.Arrays;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class FixedArgsNameFirstCommand extends NameFirstCommand {
    public abstract String[] expectedArgs();

    @Override
    public void checkArgs(String[] args) throws InvalidArgsException {
        super.checkArgs(args);
        if (!Arrays.equals(args, expectedArgs())) {
            throw new InvalidArgsException(this,
                    "This command should be called only in '" + String.join(" ", expectedArgs()) + "' form");
        }
    }
}
