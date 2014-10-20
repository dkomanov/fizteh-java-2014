package ru.fizteh.fivt.students.moskupols.cliutils;

/**
 * Created by moskupols on 28.09.14.
 */
public class CommandExecutionException extends Exception {
    public CommandExecutionException(Command cmd, String s) {
        super(cmd.name() + ": " + s);
    }
}
