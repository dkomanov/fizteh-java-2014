package ru.fizteh.fivt.students.moskupols.cliutils2.exceptions;

import ru.fizteh.fivt.students.moskupols.cliutils2.commands.Command;

/**
 * Created by moskupols on 02.12.14.
 */
public class InvalidArgsException extends CommandExecutionException {
    public InvalidArgsException(Command cmd, String message) {
        super(cmd, message);
    }
}
