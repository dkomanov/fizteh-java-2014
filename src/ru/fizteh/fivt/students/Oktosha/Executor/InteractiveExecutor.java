package ru.fizteh.fivt.students.Oktosha.Executor;

import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtility;

import java.io.Console;

/**
 * InteractiveExecutor is a child of Executor
 * which runs ConsoleUtility in interactive mode
 */
public class InteractiveExecutor {
    public void execute(ConsoleUtility utility) {
        Console c = System.console();
        String commands = c.readLine("$ ");
    }
}
