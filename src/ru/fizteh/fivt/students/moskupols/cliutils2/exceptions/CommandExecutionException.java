package ru.fizteh.fivt.students.moskupols.cliutils2.exceptions;

import ru.fizteh.fivt.students.moskupols.cliutils2.commands.Command;

/**
 * Created by moskupols on 03.12.14.
 */
public class CommandExecutionException extends Exception {
    public CommandExecutionException(Command cmd, String s) {
        this(cmd, s, null);
    }

    public CommandExecutionException(Command cmd, String s, Throwable cause) {
        super(cmd.name() + ": " + s, cause);
    }
}
