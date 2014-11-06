package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.ExitRequest;

/**
 * Base interface that encapsulates all data and commands to work with.
 * @param <S>
 *         Implementation of this interface
 */
public interface ShellState<S extends ShellState<S>> extends CommandContainer<S> {
    /**
     * Performs clean up after all work is done and the shell is going to exit.
     */
    void cleanup();

    /**
     * Makes a greeting string that can be printed
     * @return
     */
    String getGreetingString();

    /**
     * Performs all initialization work. Called when shell is starting.
     * @param host
     *         host shell that will work with this ShellState object.
     */
    void init(Shell<S> host) throws Exception;

    /**
     * Persist object's state somehow.
     * @throws Exception
     */
    void persist() throws Exception;

    /**
     * Safely exit with cleanup.
     * @param exitCode
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.ExitRequest
     *         you must throw this exception to indicate that you really want to exit. Do no call
     *         {@link System#exit(int)} instead of it.
     */
    void prepareToExit(int exitCode) throws ExitRequest;
}
