package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel;

/**
 * Interface that lets the controllable runnable to notify all waiting threads that the pause has come and
 * wait until any of the threads decides whether to continue or interrupt the execution of the runnable.
 */
@FunctionalInterface
public interface ControllableAgent {
    /**
     * Call this method if you want to make a pause.
     * @throws InterruptedException
     */
    void notifyAndWait() throws InterruptedException;
}
