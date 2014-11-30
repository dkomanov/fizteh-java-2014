package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.BAOSDuplicator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Base class for testing interpreter behaviour.
 */
public abstract class InterpreterTestBase<ShellStateImpl extends ShellState<ShellStateImpl>>
        extends TestBase {
    /**
     * '$ ', 'some-words $', etc.
     */
    protected static final String GREETING_REGEX = "(.* )?\\$ ";
    protected static PrintStream stdErr;
    // Standard out and error streams are stored here.
    private static PrintStream stdOut;
    /**
     * Here shell output can be found.
     */
    private static ByteArrayOutputStream out;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    protected Shell<ShellStateImpl> interpreter;

    @BeforeClass
    public static void globalPrepareInterpreterTestBase() {
        stdOut = System.out;
        stdErr = System.err;
        out = new BAOSDuplicator(stdOut);

        /*
      Wrap over {@link #out} that is used as {@link System#out} and {@link System#err}.
     */
        PrintStream outAndErrPrintStream = new PrintStream(out);
        System.setOut(outAndErrPrintStream);
        System.setErr(outAndErrPrintStream);
    }

    @AfterClass
    public static void globalCleanupInterpreterTestBase() {
        System.setOut(stdOut);
        System.setErr(stdErr);
    }

    protected abstract Shell<ShellStateImpl> constructInterpreter() throws TerminalException;

    /**
     * Initializes {@link #interpreter}.
     * @throws TerminalException
     */
    @Before
    public void prepare() throws TerminalException {
        interpreter = constructInterpreter();
    }

    /**
     * Removes all files under {@link #DB_ROOT}.
     * @throws java.io.IOException
     */
    @After
    public void cleanup() throws IOException {
        interpreter = null;
        stdOut.println();
        stdOut.println("-------------------------------------------------");
    }

    /**
     * Constructs a multiline regular expression that expected output must match.<br/>
     * Recommended to be used to test interpreter mode.
     * According to the contract, the format of regex is the following:<br/>
     * (greeting)(reports[0])<br/>
     * (greeting)(reports[1])<br/>
     * ...<br/>
     * (greeting)(reports[reports.length - 1])<br/>
     * (greeting)<br/>
     * @param greetingRegex
     *         Regular expression which greeting prefix must match. Greeting is printed at the beginning and
     *         after execution of each line.
     * @param reports
     *         An answer of the interpreter between two greetings. If it must be multiline, please
     *         split lines with '$' + {@link System#lineSeparator()} + '^'.
     * @return Regex for full interpreter answer.
     * @see java.util.regex.Pattern
     */
    protected String makeTerminalExpectedRegex(String greetingRegex, String... reports) {
        StringBuilder sb = new StringBuilder(String.format("(?m)^%s", greetingRegex));
        for (String s : reports) {
            sb.append(String.format("%s$%n^%s", s, greetingRegex));
        }
        return sb.toString();
    }

    /**
     * Obtains everything that was output by the interpreter.<br/>
     */
    protected String getOutput() {
        return out.toString();
    }

    /**
     * Runs batch mode with given array of commands.<br/>
     * Output is reset before run.
     * @param reinit
     *         if true, {@link #prepare()} method is called before run.
     * @param commands
     *         List of commands. Semicolons are appended to each command that does not end with a
     *         semicolon.
     * @return Exit code.
     */
    protected int runBatch(boolean reinit, String... commands) throws TerminalException {
        // Clean what has been output before.
        out.reset();

        stdOut.println(Arrays.toString(commands));
        for (int i = 0, len = commands.length; i < len; i++) {
            commands[i] = commands[i].trim();
            if (!commands[i].endsWith(";")) {
                commands[i] = commands[i] + ';';
            }
        }

        if (reinit) {
            try {
                prepare();
            } catch (TerminalException exc) {
                throw new AssertionError(exc);
            }
        }
        return interpreter.run(commands);
    }

    /**
     * Runs interpreter mode with given list of lines.<br/>
     * Output is reset before run.
     * @param reinit
     *         If true, {@link #prepare()} method is called before run.
     * @param lines
     *         List of lines. {@link System#lineSeparator()} is appended to each line.
     * @return Exit code.
     * @throws TerminalException
     */
    protected int runInteractive(boolean reinit, String... lines) throws TerminalException {
        out.reset();

        for (String cmd : lines) {
            stdOut.println(cmd);
        }
        StringBuilder sb = new StringBuilder();
        for (String cmd : lines) {
            sb.append(String.format("%s%n", cmd));
        }

        if (reinit) {
            try {
                prepare();
            } catch (TerminalException exc) {
                throw new AssertionError(exc);
            }
        }
        return interpreter.run(new ByteArrayInputStream(sb.toString().getBytes()));
    }

    protected void runInteractiveExpectZero(String... lines) throws TerminalException {
        runInteractiveExpectZero(false, lines);
    }

    protected void runInteractiveExpectNonZero(String... lines) throws TerminalException {
        runInteractiveExpectNonZero(false, lines);
    }

    protected void runBatchExpectZero(String... commands) throws TerminalException {
        runBatchExpectZero(false, commands);
    }

    protected void runBatchExpectNonZero(String... commands) throws TerminalException {
        runBatchExpectNonZero(false, commands);
    }

    protected void runInteractiveExpectZero(boolean reinit, String... lines) throws TerminalException {
        assertEquals("Exit status 0 expected", 0, runInteractive(reinit, lines));
    }

    protected void runInteractiveExpectNonZero(boolean reinit, String... lines) throws TerminalException {
        assertNotEquals("Non-zero exit status expected", 0, runInteractive(reinit, lines));
    }

    protected void runBatchExpectZero(boolean reinit, String... commands) throws TerminalException {
        assertEquals("Exit status 0 expected", 0, runBatch(reinit, commands));
    }

    protected void runBatchExpectNonZero(boolean reinit, String... commands) throws TerminalException {
        assertNotEquals("Non-zero exit status expected", 0, runBatch(reinit, commands));
    }

    /**
     * Constructs a multiline message that expected output must be equal to.<br/>
     * Recommended to be used to test batch mode.<br/>
     * Each report is considered to be a separate line. Lines are separated using {@link
     * System#lineSeparator()}.
     */
    protected String makeTerminalExpectedMessage(String... reports) {
        StringBuilder sb = new StringBuilder();
        for (String s : reports) {
            sb.append(String.format("%s%n", s));
        }
        return sb.toString();
    }
}
