package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel;

/**
 * Runnable that consumes some collections of runnables that must be executed sequentially and executes them
 * so that you can track whether some part has been executed or not.
 */
public class ControllableRunner implements Runnable {
    private static final int ORDER_NOT_SET = -1;
    private static final int ORDER_TERMINATE = 0;
    private static final int ORDER_CONTINUE = 1;

    private static final int STATUS_NOT_STARTED = -1;
    private static final int STATUS_FINISHED = 0;
    private static final int STATUS_STARTED = 1;

    private volatile ControllableRunnable runnable;

    /**
     * Order from the observer: continue or terminate. Can be also not set.
     */
    private volatile int order;

    /**
     * Current status: not started/started/finished.
     */
    private volatile int status;

    /**
     * Creates a new instance of this runner.
     */
    public ControllableRunner() {
        order = ORDER_TERMINATE;
        status = STATUS_NOT_STARTED;
    }

    /**
     * Assign the given runnable to execute it once. You can perform this action if you have never assigned
     * runnable to this runner before or the last assigned runnable has finished its execution.
     * @throws java.lang.IllegalStateException
     *         If you cannot assign any runnables now.
     */
    public synchronized void assignRunnable(ControllableRunnable runnable) throws IllegalStateException {
        if (status == STATUS_STARTED) {
            throw new IllegalStateException("Runnable has been already assigned and has not been finished");
        }
        this.runnable = runnable;
        status = STATUS_NOT_STARTED;
    }

    /**
     * Get the currently assigned controllable runnable.
     */
    public ControllableRunnable getRunnable() {
        return runnable;
    }

    /**
     * Creates and attempts to assign the alternate-kind runnable.
     * @param runnable
     *         Alternate kind of runnable.
     */
    public synchronized ControllableRunnable createAndAssign(ExceptionFreeRunnable runnable) {
        ControllableRunnable controllable = createControllable(runnable);
        assignRunnable(controllable);
        return controllable;
    }

    /**
     * @throws IllegalStateException
     *         You can call run() only if status = unstarted.
     */
    @Override
    public synchronized void run() throws IllegalStateException {
        checkRunnableAssigned();

        if (status != STATUS_NOT_STARTED) {
            throw new IllegalStateException("Can run only if status is: not started");
        }

        status = STATUS_STARTED;
        order = ORDER_CONTINUE;

        //        System.err.println("Starting");
        try {
            runnable.run();
        } finally {
            //            System.err.println("Finishing");
            status = STATUS_FINISHED;
            order = ORDER_TERMINATE;
            notifyAll();
        }
    }

    /**
     * Creates a controllable runnable that will work this this runner.
     * @param runnable
     *         alternate form of runnable - function that takes notification agent as an argument.
     */
    public ControllableRunnable createControllable(ExceptionFreeRunnable runnable) {
        return new ControllableRunnable(this) {
            @Override
            public void runWithFreedom(ControllableAgent agent) throws Exception {
                runnable.runWithFreedom(agent);
            }
        };
    }

    /**
     * Call this method if you want to wait until the next pause and play the role of observer.<br/>
     * If there are no checkpoints expected in future, this method waits until execution ends.
     * @throws InterruptedException
     * @throws java.lang.Exception
     *         If thrown during runnable execution.
     * @throws java.lang.AssertionError
     *         If thrown during runnable execution.
     */
    public synchronized void waitUntilPause() throws InterruptedException, Exception, AssertionError {
        //        System.err.println(hashCode() + ": waiting until pause");
        while (order != ORDER_NOT_SET && status != STATUS_FINISHED) {
            wait();
        }
        runnable.checkException();
    }

    /**
     * Call this method if you want to wait until execution ends. Returns immediately if the runnable is not
     * set. All pauses that can be met in the future will be ignored with order {@link #continueWork()}.<br/>
     * throws exception.
     * @throws InterruptedException
     * @throws java.lang.Exception
     *         If thrown during runnable execution.
     * @throws java.lang.AssertionError
     *         If thrown during runnable execution.
     */
    public synchronized void waitUntilEndOfWork() throws InterruptedException, Exception, AssertionError {
        while (status != STATUS_FINISHED) {
            if (order == ORDER_NOT_SET) {
                continueWork();
            }
            wait();
        }
        runnable.checkException();
    }

    /**
     * This method is called by {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support
     * .support.parallel.ControllableRunnable}
     * when it wants to pause and wait until the observer decides whether to continue work or interrupt.
     * @throws InterruptedException
     */
    synchronized void onControllablePause(ControllableRunnable pausingRunnable) throws InterruptedException {
        checkStatusIsStarted();
        if (runnable != pausingRunnable) {
            throw new IllegalStateException(
                    "Controllable runner can handle only one runnable at a time. Do not execute more than "
                    + "one runnable assigned to the same controllable runner in parallel.");
        }

        order = ORDER_NOT_SET;
        notifyAll();
        while (order == ORDER_NOT_SET) {
            wait();
        }
        if (order == ORDER_TERMINATE) {
            throw new InterruptedException("Terminated after pause because of user command.");
        }

        // For all runners that want to wait did really wait
        order = ORDER_NOT_SET;
    }

    public synchronized boolean isRunnableAssigned() {
        return runnable != null;
    }

    private synchronized void checkRunnableAssigned() {
        if (!isRunnableAssigned()) {
            throw new IllegalStateException("Runnable has not been assigned");
        }
    }

    private synchronized void checkStatusIsStarted() throws IllegalStateException {
        if (status != STATUS_STARTED) {
            throw new IllegalStateException("Can perform this only if status = started");
        }
    }

    /**
     * Call this method after waiting in {@link #waitUntilPause()} to tell the runnable to continue work.
     * @throws IllegalStateException
     *         If execution has finished.
     * @throws java.lang.Exception
     *         If thrown during runnable execution.
     * @throws java.lang.AssertionError
     *         If thrown during runnable execution.
     */
    public synchronized void continueWork() throws IllegalStateException {
        checkRunnableAssigned();
        checkStatusIsStarted();

        //        System.err.println(hashCode() + ": continue work");

        if (order == ORDER_NOT_SET) {
            order = ORDER_CONTINUE;
            notifyAll();
        } else {
            throw new IllegalStateException(
                    "Cannot manage the runnable now, because it is not in paused state.");
        }
    }

    /**
     * Call this method after waiting in {@link #waitUntilPause()} to tell the runnable to interrupt work.
     * @throws IllegalStateException
     *         If execution has finished.
     */
    public synchronized void interruptWork() throws IllegalStateException {
        checkRunnableAssigned();
        checkStatusIsStarted();

        if (order == ORDER_NOT_SET) {
            order = ORDER_TERMINATE;
            notifyAll();
        } else {
            throw new IllegalStateException(
                    "Cannot manage the runnable now, because it is not in paused state.");
        }
    }

}
