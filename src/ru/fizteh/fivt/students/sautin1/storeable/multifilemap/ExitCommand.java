package ru.fizteh.fivt.students.sautin1.storeable.multifilemap;

import ru.fizteh.fivt.students.sautin1.storeable.shell.AbstractCommand;
import ru.fizteh.fivt.students.sautin1.storeable.shell.UserInterruptException;

/**
 * "exit" command.
 * Created by sautin1 on 9/30/14.
 */
public class ExitCommand<T> extends AbstractCommand<T> {

    public ExitCommand() {
        super("exit", 0, 0);
    }

    /**
     * Throws UserInterruptException to notify the caller about user's wish to exit.
     * @param state - ShellState.
     * @param args - arguments.
     * @throws UserInterruptException always.
     */
    @Override
    public void execute(T state, String... args) throws UserInterruptException {
        throw new UserInterruptException();
    }

}
