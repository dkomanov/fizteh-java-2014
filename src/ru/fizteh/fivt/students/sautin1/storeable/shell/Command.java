package ru.fizteh.fivt.students.sautin1.storeable.shell;

/**
 * A typical command interface.
 * Created by sautin1 on 10/4/14.
 */
public interface Command<T> {
    String toString();
    int getMinArgNumber();
    int getMaxArgNumber();
    void execute(T state, String... args) throws UserInterruptException, CommandExecuteException;
}
