package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;

/**
 * Interface for exception handlers that must intercept at least all checked exceptions.
 * @param <T>
 *         type of additional data that can be given to the handler for example to make a more
 *         detailed message.
 * @author phoenix
 */
public interface AccurateExceptionHandler<T> {
    void handleException(Exception exc, T additionalData) throws TerminalException;
}
