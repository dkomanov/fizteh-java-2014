package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.SingleDatabaseShellState;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.BAOSDuplicator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test of interpreter behaviour.
 */
public class ShellTest extends TestBase {
    private static final String GREETING_REGEX = ".* \\$ ";

    /**
     * Here shell output can be found.
     */
    private static ByteArrayOutputStream out;

    // Standard out and error streams are stored here.
    private static PrintStream stdOut;
    private static PrintStream stdErr;

    /**
     * Wrap over {@link #out} that is used as {@link System#out} and {@link System#err}.
     */
    private static PrintStream outAndErrPrintStream;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    Shell<SingleDatabaseShellState> interpreter;

    @BeforeClass
    public static void globalPrepare() {
        stdOut = System.out;
        stdErr = System.err;
        out = new BAOSDuplicator(stdOut);
        System.setProperty(SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME, DB_ROOT.toString());
        outAndErrPrintStream = new PrintStream(out);
        System.setOut(outAndErrPrintStream);
        System.setErr(outAndErrPrintStream);
    }

    @AfterClass
    public static void globalCleanup() {
        System.setOut(stdOut);
        System.setErr(stdErr);
    }

    @Before
    public void prepare() throws TerminalException {
        interpreter = new Shell<>(new SingleDatabaseShellState());
    }

    @After
    public void cleanup() throws IOException {
        interpreter = null;
        cleanDBRoot();
        stdOut.println();
        stdOut.println("-------------------------------------------------");
    }

    private String getOutput() {
        return out.toString();
    }

    private int runBatch(boolean reinit, String... commands) {
        // Clean what has been output before.
        out.reset();

        stdOut.println(Arrays.toString(commands));
        for (int i = 0, len = commands.length; i < len; i++) {
            commands[i] = commands[i].trim();
            if (!commands[i].endsWith(";")) {
                commands[i] = commands[i] + ";";
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

    private int runInteractive(boolean reinit, String... lines) throws TerminalException {
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

    private void runInteractiveExpectZero(String... lines) throws TerminalException {
        runInteractiveExpectZero(false, lines);
    }

    private void runInteractiveExpectNonZero(String... lines) throws TerminalException {
        runInteractiveExpectNonZero(false, lines);
    }

    private void runBatchExpectZero(String... commands) {
        runBatchExpectZero(false, commands);
    }

    private void runBatchExpectNonZero(String... commands) {
        runBatchExpectNonZero(false, commands);
    }

    private void runInteractiveExpectZero(boolean reinit, String... lines)
            throws TerminalException {
        assertEquals("Exit status 0 expected", 0, runInteractive(reinit, lines));
    }

    private void runInteractiveExpectNonZero(boolean reinit, String... lines)
            throws TerminalException {
        assertNotEquals("Non-zero exit status expected", 0, runInteractive(reinit, lines));
    }

    private void runBatchExpectZero(boolean reinit, String... commands) {
        assertEquals("Exit status 0 expected", 0, runBatch(reinit, commands));
    }

    private void runBatchExpectNonZero(boolean reinit, String... commands) {
        assertNotEquals("Non-zero exit status expected", 0, runBatch(reinit, commands));
    }

    private void createAndUseTable(String table) {
        runBatchExpectZero(String.format("create %1$s; use %1$s", table));
        assertEquals(
                "Creating and using talbe",
                String.format("created%nusing %1$s%n", table),
                getOutput());
    }

    @Test
    public void testNotExistingCommand() {
        String name = "command_not_exists";
        runBatchExpectNonZero(name);
        assertEquals(
                "Calling an not existing command must raise an error",
                String.format("%s: command is missing%n", name),
                getOutput());
    }

    @Test
    public void testWithWrongArgumentsNumber() {
        String command = "create 1 2 3 4";
        runBatchExpectNonZero(command);
        assertThat(
                "Wrong arguments error must be raised",
                getOutput(),
                startsWith("Wrong arguments number"));
    }

    @Test
    public void testCreateTableWithInvalidName() {
        String command = "create " + Paths.get("..", "table");
        runBatchExpectNonZero(command);
        assertEquals(
                "Illegal table name error must be raised",
                String.format("Table name is not correct%n"),
                getOutput());
    }

    @Test
    public void testCreateTableWithInvalidName1() {
        String command = "create " + Paths.get("..", DB_ROOT.getFileName().toString(), "table");
        runBatchExpectNonZero(command);
        assertEquals(
                "Illegal table name error must be raised",
                String.format("Table name is not correct%n"),
                getOutput());
    }

    @Test
    public void testCreateTableWithInvalidName2() {
        String command = "create " + Paths.get("outside", "inside");
        runBatchExpectNonZero(command);
        assertEquals(
                "Illegal table name error must be raised",
                String.format("Table name is not correct%n"),
                getOutput());
    }

    @Test
    public void testUseNotExistingTable() {
        String name = "not_existing_table";
        runBatchExpectNonZero("use " + name);
        assertEquals(
                "Attempt to use not existing table must raise error",
                String.format("Table %s not exists%n", name),
                getOutput());
    }

    @Test
    public void testDropNotExistingTable() {
        String name = "not_existing_table";
        String command = "drop " + name;
        runBatchExpectNonZero(command);
        assertEquals(
                "Attempt to use not existing table must raise error",
                String.format("Table %s not exists%n", name),
                getOutput());
    }

    @Test
    public void testCreateTable() {
        String name = "existing_table";
        String command = "create " + name;

        runBatchExpectZero(true, command);
        assertEquals(
                "Attempt to create not existing table", String.format("created%n"), getOutput());
        runBatchExpectNonZero(true, command);
        assertEquals(
                "Attempt to create existing table",
                String.format("Table %s exists%n", name),
                getOutput());
    }

    @Test
    public void testUseWithUncommittedChanges() {
        String tableA = "tableA";
        String tableB = "tableB";

        createAndUseTable(tableB);
        createAndUseTable(tableA);

        String command = String.format(
                "put a b; put b c; put c d; remove b; put a bbb; use %s", tableB);
        String expectedReply = String.format(
                "new%nnew%nnew%nremoved%noverwrite%nold b%n2 unsaved changes%n");

        runBatchExpectNonZero(command);

        assertEquals(
                "Attempt to use another table with uncommitted changes",
                expectedReply,
                getOutput());
    }

    @Test
    public void testUseWithTheSameTableAndUncommittedChanges() {
        String tableA = "tableA";
        String tableB = "tableB";

        createAndUseTable(tableB);
        createAndUseTable(tableA);

        String command = String.format(
                "put a b; put b c; put c d; remove b; put a bbb; use %1$s", tableA);
        String expectedReply = String.format(
                "new%nnew%nnew%nremoved%noverwrite%nold b%nusing %1$s%n", tableA);

        runBatchExpectZero(false, command);

        assertEquals(
                "Attempt to use the same table with uncommitted changes",
                expectedReply,
                getOutput());
    }

    @Test
    public void testCommit() {
        String table = "table";

        String command = String.format(
                "put a b; commit; put b c; remove a; commit", table);
        String expectedReply = String.format("new%n1%nnew%nremoved%n2%n");

        createAndUseTable(table);
        runBatchExpectZero(command);

        assertEquals("Changes count test in commit", expectedReply, getOutput());
    }

    @Test
    public void testRollback() {
        String table = "table";

        String command = String.format(
                "put a b; commit; put b c; remove a; rollback", table);
        String expectedReply = String.format("new%n1%nnew%nremoved%n2%n");

        createAndUseTable(table);
        runBatchExpectZero(command);

        assertEquals("Changes count test in rollback", expectedReply, getOutput());
    }

    @Test
    public void testGetNotExistent() {
        String table = "table";
        String key = "not_existent_key";

        String command = String.format("get %2$s", table, key);
        String expectedReply = String.format("not found%n");

        createAndUseTable(table);
        runBatchExpectNonZero(command);

        assertEquals("Getting not existent key must raise error", expectedReply, getOutput());
    }

    @Test
    public void testGetExistent() {
        String table = "table";

        String key = "key";
        String value = "value";

        String command = String.format("put %1$s %2$s; get %1$s", key, value);
        String expectedReply = String.format("new%nfound%n%s%n", value);

        createAndUseTable(table);
        runBatchExpectZero(command);

        assertEquals("Getting existent key", expectedReply, getOutput());
    }

    @Test
    public void testRemoveNotExistent() {
        String table = "table";
        createAndUseTable(table);

        String key = "key";

        String command = String.format("remove %s", key);
        String expectedReply = String.format("not found%n");

        runBatchExpectNonZero(command);

        assertEquals("Removing not existent key", expectedReply, getOutput());
    }

    @Test
    public void testExit() {
        String command = String.format("exit; unknown_command");

        runBatchExpectZero(command);

        assertEquals("Start and exit - no output must be", "", getOutput());
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
     * @param reports
     *         An answer of the interpreter between two greetings. If it must me multiline, please
     *         split lines with '$' + {@link System#lineSeparator()} + '^'.
     * @return Regex for full interpreter answer.
     * @see java.util.regex.Pattern
     */
    private String makeTerminalExpectedRegex(String greetingRegex, String... reports) {
        StringBuilder sb = new StringBuilder(String.format("(?m)^%s", greetingRegex));
        for (String s : reports) {
            sb.append(String.format("%s$%n^%s", s, greetingRegex));
        }
        return sb.toString();
    }

    /**
     * Constructs a multiline message that expected output must be equal to.<br/>
     * Recommended to be used to test batch mode.<br/>
     * Each report is considered to be a separate line. Lines are separated using {@link
     * System#lineSeparator()}.
     * @param reports
     * @return
     */
    private String makeTerminalExpectedMessage(String... reports) {
        StringBuilder sb = new StringBuilder();
        for (String s : reports) {
            sb.append(String.format("%s%n", s));
        }
        return sb.toString();
    }

    @Test
    public void testInteractiveMode() throws TerminalException {
        runInteractiveExpectZero("exit", "exit");
        assertTrue(
                "Greeting format is not honoured",
                getOutput().matches(makeTerminalExpectedRegex(GREETING_REGEX)));
    }

    @Test
    public void testInteractiveMode1() throws TerminalException {
        String table = "table";
        String fakeTable = "fake_table";
        createAndUseTable(table);

        runBatchExpectZero(false, "put a b; put b c; put c d; put d e; put e a; exit");
        runInteractiveExpectZero(
                "use " + table,
                "show tables",
                "use " + fakeTable + "; list",
                "use " + table + "; list");

        String regex = makeTerminalExpectedRegex(
                GREETING_REGEX,
                "using " + table,
                String.format("table_name row_count$%n^%s 5", table),
                String.format("Table %s not exists", fakeTable),
                String.format("using %s$%n^a, b, c, d, e", table));
        stdErr.println(regex);
        assertTrue("Interactive mode test fail", getOutput().matches(regex));
    }

    @Test
    public void testRunShellWithNullStream() throws TerminalException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Input stream must not be null");
        interpreter.run((InputStream) null);
    }

    @Test
    public void testDropExistingTable() {
        String table = "table";
        runBatchExpectZero(true, "create " + table);
        runBatchExpectZero(true, "drop " + table);
        assertEquals(
                "When an existing table is dropped, a good report must be made",
                String.format("dropped%n"),
                getOutput());
    }

    @Test
    public void testSize() {
        String table = "table";
        runBatchExpectZero(
                "create " + table,
                "use " + table,
                "put a b; put c d; put d e; remove c; put d dd; put k a");
        runBatchExpectZero("size");
        assertEquals("Improper size printed", String.format("3%n"), getOutput());
    }

    @Test
    public void testShowUnexpectedOption() {
        String option = "what?";
        runBatchExpectNonZero("show " + option);
        assertEquals(
                "Running 'show' with illegal option must raise error",
                String.format("show: unexpected option: %s%n", option),
                getOutput());
    }

    @Test
    public void testInitWithFarNotExistingDir() throws TerminalException {
        System.setProperty(
                SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME,
                DB_ROOT.resolve("path1").resolve("path2").toString());

        exception.expect(TerminalException.class);
        exception
                .expectMessage("Database directory parent path does not exist or is not a directory");

        try {
            interpreter = new Shell<>(new SingleDatabaseShellState());
        } finally {
            System.setProperty(
                    SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME, DB_ROOT.toString());
        }
    }

    @Test
    public void testInitWithNullDir() throws TerminalException {
        System.getProperties().remove(SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME);

        exception.expect(TerminalException.class);
        exception.expectMessage("Please mention database directory");

        try {
            interpreter = new Shell<>(new SingleDatabaseShellState());
        } finally {
            System.setProperty(
                    SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME, DB_ROOT.toString());
        }
    }

    @Test
    public void testCommitWithNoActiveTable() {
        runBatchExpectZero("commit");

        assertEquals(
                "Commit with no active table must return 0 changes",
                makeTerminalExpectedMessage("0"),
                getOutput());
    }

    @Test
    public void testRollbackWithNoActiveTable() {
        runBatchExpectZero("rollback");

        assertEquals(
                "Rollback with no active table must return 0 changes",
                makeTerminalExpectedMessage("0"),
                getOutput());
    }

    @Test
    public void testDropActiveTable() {
        String table = "table";

        runBatchExpectZero("create " + table, "use " + table, "drop " + table);

        assertEquals(
                "Create + use + drop gives illegal report",
                makeTerminalExpectedMessage("created", "using " + table, "dropped"),
                getOutput());

        runBatchExpectNonZero("use " + table);
    }

    @Test
    public void testShowCorruptTables() throws IOException, TerminalException {
        String tableA = "tableA";
        String tableB = "tableB";
        String corruptTable = "corruptTable";

        runBatchExpectZero(
                "create " + tableA,
                "create " + tableB,
                "create " + corruptTable,
                "use " + tableA,
                "put a b; put c d",
                "commit",
                "use " + corruptTable,
                "put 1 2; put 2 3; put 3 4; put 4 5");

        Path corruptPath = DB_ROOT.resolve(corruptTable).resolve("1.dir").resolve("1.dat");
        if (!Files.exists(corruptPath.getParent())) {
            Files.createDirectory(corruptPath.getParent());
        }
        try (FileOutputStream fos = new FileOutputStream(corruptPath.toString())) {
            fos.write("failure".getBytes());
        }

        runInteractiveExpectZero(true, "show tables");

        // Table order can be different.
        String lineRegex = String.format("(%s 2|%s 0|%s corrupt)", tableA, tableB, corruptTable);
        assertTrue(
                "Corrupt tables must be marked properly", getOutput().matches(
                        makeTerminalExpectedRegex(
                                GREETING_REGEX,
                                String.format("table_name row_count$(%n^%s$){3}", lineRegex))));
    }

    @Test
    public void testUseCorruptTable() throws IOException {
        String table = "corruptTable";

        createAndUseTable(table);
        runBatchExpectZero("put a b; put c d; put e f; put k j");

        Path corruptPath = DB_ROOT.resolve(table).resolve("1.dir").resolve("1.dat");
        if (!Files.exists(corruptPath.getParent())) {
            Files.createDirectory(corruptPath.getParent());
        }
        try (FileOutputStream fos = new FileOutputStream(corruptPath.toString())) {
            fos.write(new byte[] {1, 2, 5, 0});
        }

        runBatchExpectNonZero(true, "use " + table);
        assertEquals(
                "Using corrupt table must raise error",
                makeTerminalExpectedMessage("Table " + table + " is corrupt"),
                getOutput());
    }
}
