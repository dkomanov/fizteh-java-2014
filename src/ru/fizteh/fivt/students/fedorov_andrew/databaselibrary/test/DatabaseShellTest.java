package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.SingleDatabaseShellState;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for database using {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell}
 * in batch and interactive mode.
 */
@RunWith(JUnit4.class)
public class DatabaseShellTest extends InterpreterTestBase<SingleDatabaseShellState> {
    @BeforeClass
    public static void globalPrepareDatabaseShellTest() {
        System.setProperty(SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME, DB_ROOT.toString());
    }

    private void createAndUseTable(String table) {
        runBatchExpectZero(String.format("create %1$s; use %1$s", table));
        assertEquals(
                "Creating and using table",
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

    @Override
    protected Shell<SingleDatabaseShellState> constructInterpreter() throws TerminalException {
        return new Shell<>(new SingleDatabaseShellState());
    }

    @After
    @Override
    public void cleanup() throws IOException {
        super.cleanup();
        cleanDBRoot();
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
                String.format("%s not exists", fakeTable),
                String.format("using %s$%n^a, b, c, d, e", table));
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
    public void testListWithoutActiveTable() {
        runBatchExpectNonZero("list");
        assertEquals(
                "Calling method that uses table without active table must raise error",
                getOutput(),
                makeTerminalExpectedMessage("no table"));
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
        assertThat(
                "Using corrupt table must raise error",
                getOutput(),
                startsWith("Table " + table + " is corrupt"));
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
                String.format("%s not exists%n", name),
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
}
