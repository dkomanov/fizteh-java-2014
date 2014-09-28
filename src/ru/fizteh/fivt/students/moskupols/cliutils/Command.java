package ru.fizteh.fivt.students.moskupols.cliutils;

/**
 * Created by moskupols on 28.09.14.
 */
public interface Command {
    void execute() throws CommandExecutionException, StopProcessingException;
}
