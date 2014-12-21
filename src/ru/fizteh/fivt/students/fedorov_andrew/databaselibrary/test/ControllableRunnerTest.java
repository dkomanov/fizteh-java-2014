package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel.ControllableAgent;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel.ControllableRunnable;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel.ControllableRunner;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ControllableRunnerTest extends DuplicatedIOTestBase {
    private static final String NEW_LINE = System.lineSeparator();

    @Test
    public void testWaitForEndOfWork() throws Exception {
        ControllableRunner runner = new ControllableRunner();
        ControllableRunnable runnable = runner.createControllable(
                (ControllableAgent agent) -> {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException exc) {
                        throw new AssertionError(exc);
                    }
                    System.err.println("Hello from runnable");
                });
        runner.assignRunnable(runnable);

        new Thread(runner, "Runnable").start();
        runner.waitUntilPause();

        System.err.println("After execution");

        assertEquals("Hello from runnable" + NEW_LINE + "After execution" + NEW_LINE, getOutput());
    }

    @Test
    public void testWaitForEndOfWork1() throws Exception {
        ControllableRunner runner = new ControllableRunner();
        ControllableRunnable runnable = runner.createControllable(
                (ControllableAgent agent) -> System.err.println("Hello from runnable"));
        runner.assignRunnable(runnable);

        new Thread(runner, "Runnable").start();

        // Possibly giving time to finish;
        Thread.sleep(2000L);
        runner.waitUntilPause();

        System.err.println("After execution");

        assertEquals("Hello from runnable" + NEW_LINE + "After execution" + NEW_LINE, getOutput());
    }

    @Test
    public void testContinueInCheckpoint() throws Throwable {
        ControllableRunner runner = new ControllableRunner();
        ControllableRunnable runnable = runner.createControllable(
                (ControllableAgent agent) -> {
                    System.err.println("Before checkpoint");
                    try {
                        agent.notifyAndWait();
                    } catch (InterruptedException exc) {
                        throw new AssertionError(exc);
                    }
                    System.err.println("After checkpoint");
                });
        runner.assignRunnable(runnable);

        new Thread(runner, "Runnable").start();
        runner.waitUntilPause();
        System.err.println("In checkpoint");
        runner.continueWork();
        runner.waitUntilPause();
        System.err.println("After execution");

        assertEquals(
                String.join(
                        NEW_LINE,
                        "Before checkpoint",
                        "In checkpoint",
                        "After checkpoint",
                        "After execution",
                        ""), getOutput());
    }

    @Test
    public void testInterruptInCheckpoint() throws InterruptedException, Exception {
        ControllableRunner runner = new ControllableRunner();
        ControllableRunnable runnable = runner.createControllable(
                (ControllableAgent agent) -> {
                    System.err.println("Before checkpoint");
                    try {
                        agent.notifyAndWait();
                    } catch (InterruptedException exc) {
                        System.err.println("Interrupted");
                        return;
                    }
                    System.err.println("Must not be written");
                });
        runner.assignRunnable(runnable);

        new Thread(runner, "Runnable").start();
        runner.waitUntilPause();
        System.err.println("In checkpoint");
        runner.interruptWork();
        runner.waitUntilPause(); // Waiting until execution ends.
        System.err.println("After execution");

        assertEquals(
                String.join(
                        NEW_LINE, "Before checkpoint", "In checkpoint", "Interrupted", "After execution", ""),
                getOutput());
    }
}
