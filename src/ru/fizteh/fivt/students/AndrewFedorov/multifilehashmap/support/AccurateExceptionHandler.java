package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support;

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
    public void handleException(Exception exc, T additionalData);
}
