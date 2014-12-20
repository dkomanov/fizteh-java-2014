package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.BAOSDuplicator;

import java.io.PrintStream;

/**
 * Test base for convenient output tracking. {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary
 * .test.support.BAOSDuplicator}
 * is used for output duplicating.
 */
@Ignore
public class DuplicatedIOTestBase {
    protected static PrintStream stdErr;
    // Standard out and error streams are stored here.
    private static PrintStream stdOut;

    private static BAOSDuplicator out;

    /**
     * Sets standard output and error stream as {@link ru.fizteh.fivt.students.fedorov_andrew
     * .databaselibrary.test.support.BAOSDuplicator}.
     */
    @BeforeClass
    public static void globalPrepareDuplicatedIOTestBase() {
        stdOut = System.out;
        stdErr = System.err;
        out = new BAOSDuplicator(stdOut);

        // Wrap over {@link #out} that is used as {@link System#out} and {@link System#err}.
        PrintStream outAndErrPrintStream = new PrintStream(out);
        System.setOut(outAndErrPrintStream);
        System.setErr(outAndErrPrintStream);
    }

    /**
     * Recovers standard output and error streams.
     */
    @AfterClass
    public static void globalCleanupDuplicatedIOTestBase() {
        System.setOut(stdOut);
        System.setErr(stdErr);
    }

    /**
     * Obtains output from the buffer.
     */
    public String getOutput() {
        return out.toString();
    }

    public void printDirectlyToStdOut(Object obj) {
        stdOut.print(obj);
    }

    public void printlnDirectlyToStdOut(Object obj) {
        stdOut.println(obj);
    }

    public void printDirectlyToStdOut(String str) {
        stdOut.print(str);
    }

    public void printlnDirectlyToStdOut(String str) {
        stdOut.println(str);
    }

    public void printlnDirectlyToStdOut() {
        stdOut.println();
    }

    /**
     * Resets output in the buffer.
     */
    @Before
    public void prepare() {
        out.reset();
    }

    /**
     * Prints to the standard output test separator string.
     */
    @After
    public void cleanup() {
        printlnDirectlyToStdOut();
        printlnDirectlyToStdOut("-------------------------------------------------");
    }

}
