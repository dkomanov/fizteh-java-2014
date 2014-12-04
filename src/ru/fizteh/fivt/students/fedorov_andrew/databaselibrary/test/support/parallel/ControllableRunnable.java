package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel;

/**
 * Base class for runnables served by this runner.
 */
public abstract class ControllableRunnable implements Runnable, ControllableAgent, ExceptionFreeRunnable {
    private final ControllableRunner host;

    private volatile Throwable exception;

    public ControllableRunnable(ControllableRunner host) {
        this.host = host;
    }

    @Override
    public final void run() {
        synchronized (this) {
            exception = null;
        }
        try {
            runWithFreedom(this::notifyAndWait);
        } catch (Exception | AssertionError exc) {
            synchronized (this) {
                exception = exc;
            }
        }
    }

    /**
     * Call this method after execution finishes. If an {@link Exception} or {@link
     * AssertionError} has occurred during execution, it will
     * be rethrown.
     * @throws Exception
     */
    public final synchronized void checkException() throws Exception, AssertionError {
        if (exception != null) {
            if (exception instanceof Exception) {
                throw (Exception) exception;
            } else if (exception instanceof AssertionError) {
                throw (AssertionError) exception;
            } else {
                throw new Error("Some fatal exception occurred during thread execution", exception);
            }
        }
    }

    @Override
    public final void notifyAndWait() throws InterruptedException {
        host.onControllablePause(this);
    }
}
