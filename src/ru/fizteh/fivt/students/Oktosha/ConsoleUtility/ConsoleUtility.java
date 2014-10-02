package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

import ru.fizteh.fivt.students.Oktosha.Command.Command;

/**
 * ConsoleUtility is an interface which should be implemented by Shell,
 * DbMain or whatever else ConsoleUtility run by Executor.
 */
public interface ConsoleUtility {
    void run(Command cmd) throws CommandIsNotSupportedException,
                                 CommandArgumentSyntaxException;
}
