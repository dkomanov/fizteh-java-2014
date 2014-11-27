package ru.fizteh.fivt.students.moskupols.cliutils;

/**
 * Created by moskupols on 28.09.14.
 */
public class CommandExecutionException extends Exception {
    public CommandExecutionException(Command cmd, String s) {
        this(cmd, s, null);
    }

    public CommandExecutionException(Command cmd, String s, Throwable cause) {
        super(cmd.name() + ": " + s, cause);
    }
}
