package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 12.12.14.
 */
public interface Command {
    String name();

    void perform(Object context, String[] args) throws CommandExecutionException;
}
