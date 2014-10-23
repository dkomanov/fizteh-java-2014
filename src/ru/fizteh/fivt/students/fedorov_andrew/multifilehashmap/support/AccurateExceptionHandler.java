package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.support;

import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.TerminalException;

/**
 * Interface for exception handlers that must intercept at least all checked
 * exceptions.
 * 
 * @author phoenix
 * 
 * @param <T>
 *            type of additional data that can be given to the handler for
 *            example to make a more detailed message.
 */
public interface AccurateExceptionHandler<T> {
    public void handleException(Exception exc, T additionalData)
	    throws TerminalException;
}
