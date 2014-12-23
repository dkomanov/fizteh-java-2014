package ru.fizteh.fivt.students.moskupols.cliutils2.interpreters;

import ru.fizteh.fivt.students.moskupols.cliutils.UnknownCommandException;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 12.12.14.
 */
public interface Interpreter {
    void interpret() throws CommandExecutionException, UnknownCommandException;
}
