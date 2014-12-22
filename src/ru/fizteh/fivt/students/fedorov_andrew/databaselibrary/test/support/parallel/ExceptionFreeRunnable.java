package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel;

/**
 * Interface for runnable that can throw any exceptions.
 */
@FunctionalInterface
public interface ExceptionFreeRunnable {
    /**
     * Place your implementation here and do not care of exceptions. The given throwables will be caught and
     * become accessible.
     * @throws Exception
     * @throws AssertionError
     */
    void runWithFreedom(ControllableAgent agent) throws Exception, AssertionError;
}
