package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.SingleDatabaseShellState;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.MutatedSDSS;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for database using {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell
 * .Shell}
 * in batch and interactive mode.
 */
@RunWith(JUnit4.class)
public class DatabaseShellTest extends InterpreterTestBase<SingleDatabaseShellState> {
    @BeforeClass
    public static void globalPrepareDatabaseShellTest() {
        System.setProperty(SingleDatabaseShellState.DB_DIRECTORY_PROPERTY_NAME, DB_ROOT.toString());
    }

    private void createTableWithStringColumn(String table) throws TerminalException {
        runBatchExpectZero(String.format("create %1$s (String)", table));
        assertEquals(
                "Creating table", makeTerminalExpectedMessage("created"), getOutput());
    }

    @Test
    public void testFailPersistOnExit() throws TerminalException {
        MutatedSDSS state = new MutatedSDSS(0);
        interpreter = new Shell<>(state);

        runBatchExpectNonZero("exit");
        assertEquals(
                "Failing on persist() call",
                makeTerminalExpectedMessage("Fail on commit [test mode]"),
                getOutput());
    }

    @Test
    public void testPutCompositeStoreable() throws TerminalException {
        runBatchExpectZero("create t1 (String  int boolean)", "use t1", "put key [ \"hello\", 2, null ]");

        assertThat("Output after put", getOutput(), containsString(makeTerminalExpectedMessage("new")));

        runBatchExpectZero("use t1", "get key");

        assertThat(
                "Output after get", getOutput(), containsString(
                        makeTerminalExpectedMessage(
                                "found", "[\"hello\",2,null]")));
    }

    @Test
    public void testNotExistingCommand() throws TerminalException {
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
    public void testUseWithUncommittedChanges() throws TerminalException {
        String tableA = "tableA";
        String tableB = "tableB";

        createTableWithStringColumn(tableB);
        createTableWithStringColumn(tableA);

        String command = String.format(
                "use " + tableA
                + "; put a [\"b\"]; put b [\"c\"]; put c [\"d\"]; remove b; put a [\"bbb\"]; use %s", tableB);
        String expectedReply = String.format(
                "using tableA%nnew%nnew%nnew%nremoved%noverwrite%nold [\"b\"]%n2 unsaved changes%n");

        runBatchExpectNonZero(command);

        assertEquals("Attempt to use another table with uncommitted changes", expectedReply, getOutput());
    }

    @Test
    public void testUseWithTheSameTableAndUncommittedChanges() throws TerminalException {
        String tableA = "tableA";
        String tableB = "tableB";

        createTableWithStringColumn(tableB);
        createTableWithStringColumn(tableA);

        runBatchExpectZero(
                "use " + tableA,
                "put a [\"b\"]; put b [\"c\"]; put c [\"d\"]; remove b; put a [\"bbb\"];",
                "use " + tableA);

        assertEquals(
                makeTerminalExpectedMessage(
                        "using " + tableA,
                        "new",
                        "new",
                        "new",
                        "removed",
                        "overwrite",
                        "old [\"b\"]",
                        "using " + tableA), getOutput());
    }

    @Test
    public void testCommit() throws TerminalException {
        String table = "table";

        createTableWithStringColumn(table);
        runBatchExpectZero("use " + table, "put a [\"b\"]; commit; put b [\"c\"]; remove a; commit");

        assertEquals(
                makeTerminalExpectedMessage("using " + table, "new", "1", "new", "removed", "2"),
                getOutput());
    }

    @Test
    public void testCommitFail() throws TerminalException {
        interpreter = new Shell<>(new MutatedSDSS(0));

        runBatchExpectNonZero("create t1 (String)", "use t1", "put a [\"b\"]", "commit");

        assertThat(getOutput(), containsString("Fail on commit [test mode]"));
    }

    @Test
    public void testRollback() throws TerminalException {
        String table = "table";

        createTableWithStringColumn(table);
        runBatchExpectZero("use table", "put a [\"b\"]", "commit", "put b [\"c\"];", "remove a", "rollback");

        assertEquals(
                makeTerminalExpectedMessage("using " + table, "new", "1", "new", "removed", "2"),
                getOutput());
    }

    @Test
    public void testGetNotExistent() throws TerminalException {
        String table = "table";
        String key = "not_existent_key";

        createTableWithStringColumn(table);
        runBatchExpectNonZero("use " + table, "get " + key);

        assertEquals(makeTerminalExpectedMessage("using " + table, "not found"), getOutput());
    }

    @Test
    public void testGetExistent() throws TerminalException {
        String table = "table";

        String key = "key";
        String value = "value";

        createTableWithStringColumn(table);
        runBatchExpectZero("use " + table, "put " + key + " [\"" + value + "\"]", "get " + key);

        assertEquals(
                makeTerminalExpectedMessage(
                        "using " + table, "new", "found", "[\"" + value + "\"]"), getOutput());
    }

    @Test
    public void testRemoveNotExistent() throws TerminalException {
        String table = "table";
        createTableWithStringColumn(table);

        String key = "key";

        runBatchExpectNonZero("use " + table, "remove " + key);

        assertEquals(makeTerminalExpectedMessage("using " + table, "not found"), getOutput());
    }

    @Test
    public void testExit() throws TerminalException {
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
        createTableWithStringColumn(table);

        runBatchExpectZero(
                "use " + table,
                "put a [\"b\"]; put b [\"c\"]; put c [\"d\"]; put d [\"e\"]; put e [\"a\"];",
                "exit");
        runInteractiveExpectZero(
                "use " + table, "show tables", "use " + fakeTable + "; list", "use " + table + "; list");

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
    public void testDropExistingTable() throws TerminalException {
        String table = "table";
        createTableWithStringColumn(table);
        runBatchExpectZero("drop " + table);
        assertEquals(
                "When an existing table is dropped, a good report must be made",
                String.format("dropped%n"),
                getOutput());
    }

    @Test
    public void testListWithoutActiveTable() throws TerminalException {
        runBatchExpectNonZero("list");
        assertEquals(
                "Calling method that uses table without active table must raise error",
                getOutput(),
                makeTerminalExpectedMessage("no table"));
    }

    @Test
    public void testSize() throws TerminalException {
        String table = "table";
        createTableWithStringColumn(table);
        runBatchExpectZero(
                "use " + table,
                "put a [\"b\"]; put c [\"d\"]; put d [\"e\"]; remove c; put d [\"dd\"]; put k [\"a\"]");
        runBatchExpectZero("use " + table, "size");
        assertEquals(makeTerminalExpectedMessage("using " + table, "3"), getOutput());
    }

    @Test
    public void testShowUnexpectedOption() throws TerminalException {
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
        exception.expectMessage(
                "Database directory parent path does not exist or is not a " + "directory");

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
    public void testCommitWithNoActiveTable() throws TerminalException {
        runBatchExpectZero("commit");

        assertEquals(
                "Commit with no active table must return 0 changes",
                makeTerminalExpectedMessage("0"),
                getOutput());
    }

    @Test
    public void testRollbackWithNoActiveTable() throws TerminalException {
        runBatchExpectZero("rollback");

        assertEquals(
                "Rollback with no active table must return 0 changes",
                makeTerminalExpectedMessage("0"),
                getOutput());
    }

    @Test
    public void testDropActiveTable() throws TerminalException {
        String table = "table";

        runBatchExpectZero("create " + table + " (String)", "use " + table, "drop " + table);

        assertEquals(
                "Create + use + drop gives illegal report",
                makeTerminalExpectedMessage("created", "using " + table, "dropped"),
                getOutput());

        runBatchExpectNonZero("use " + table);
    }

    @Test
    public void testShowCorruptTables() throws Exception {
        String tableA = "tableA";
        String tableB = "tableB";
        String corruptTable = "corruptTable";

        createTableWithStringColumn(corruptTable);
        createTableWithStringColumn(tableB);
        createTableWithStringColumn(tableA);

        runBatchExpectZero(
                "use " + tableA,
                "put a [\"b\"]; put c [\"d\"]",
                "commit",
                "use " + corruptTable,
                "put 1 [\"2\"]; put 2 [\"3\"]; put 3 [\"4\"]; put 4 [\"5\"]");

        Path corruptPath = DB_ROOT.resolve(corruptTable).resolve("1.dir").resolve("1.dat");
        if (!Files.exists(corruptPath.getParent())) {
            Files.createDirectory(corruptPath.getParent());
        }
        try (FileOutputStream fos = new FileOutputStream(corruptPath.toString())) {
            fos.write("failure".getBytes());
        }

        runInteractiveExpectZero("show tables");

        // Table order can be different.
        String lineRegex = String.format("(%s 2|%s 0|%s corrupt)", tableA, tableB, corruptTable);
        assertTrue(
                "Corrupt tables must be marked properly", getOutput().matches(
                        makeTerminalExpectedRegex(
                                GREETING_REGEX,
                                String.format("table_name row_count$(%n^%s$){3}", lineRegex))));
    }

    @Test
    public void testUseCorruptTable() throws IOException, TerminalException {
        String table = "corruptTable";

        createTableWithStringColumn(table);
        runBatchExpectZero("use " + table, "put a [\"b\"]; put c [\"d\"]; put e [\"f\"]; put k [\"j\"]");

        Path corruptPath = DB_ROOT.resolve(table).resolve("1.dir").resolve("1.dat");
        if (!Files.exists(corruptPath.getParent())) {
            Files.createDirectory(corruptPath.getParent());
        }
        try (FileOutputStream fos = new FileOutputStream(corruptPath.toString())) {
            fos.write(new byte[] {1, 2, 5, 0});
        }

        runBatchExpectNonZero("use " + table);
        assertThat(
                "Using corrupt table must raise error",
                getOutput(),
                startsWith("Table " + table + " is corrupt"));
    }

    @Test
    public void testWithWrongArgumentsNumber() throws TerminalException {
        String command = "get 1 2 3 4";
        runBatchExpectNonZero(command);
        assertThat(
                "Wrong arguments error must be raised", getOutput(), startsWith("Wrong arguments number"));
    }

    @Test
    public void testCreateTableWithInvalidName() throws TerminalException {
        runBatchExpectNonZero("create " + Paths.get("..", "table").toString() + " (String)");
        assertEquals(
                "Illegal table name error must be raised",
                String.format("Table name is not correct%n"),
                getOutput());
    }

    @Test
    public void testRunInteractiveWithUnparseableStream() throws TerminalException {
        exception.expect(TerminalException.class);
        exception.expectMessage(containsString("Error in input stream"));

        runInteractiveExpectNonZero("do smth \"");
    }

    @Test
    public void testRunWithUnparseableStream() throws TerminalException {
        exception.expect(TerminalException.class);
        exception.expectMessage(containsString("Cannot parse command arguments"));

        runBatchExpectNonZero("do smth \""); // Unclosed quotes.
    }

    @Test
    public void testCreateTableWithInvalidName1() throws TerminalException {
        runBatchExpectNonZero(
                "create " + Paths.get("..", DB_ROOT.getFileName().toString(), "table").toString()
                + " (String)");
        assertEquals(
                "Illegal table name error must be raised",
                String.format("Table name is not correct%n"),
                getOutput());
    }

    @Test
    public void testCreateTableWithInvalidName2() throws TerminalException {
        runBatchExpectNonZero(
                "create " + Paths.get("outside", "inside").toString() + " (String)");
        assertEquals(
                "Illegal table name error must be raised",
                makeTerminalExpectedMessage("Table name is not correct"),
                getOutput());
    }

    @Test
    public void testCreateTableWithoutTypes() throws TerminalException {
        runBatchExpectNonZero("create table one two three");
        assertThat(
                "Calling create command without column types",
                getOutput(),
                containsString("Round brackets must exist and contain types list inside them"));
    }

    @Test
    public void testUseNotExistingTable() throws TerminalException {
        String name = "not_existing_table";
        runBatchExpectNonZero("use " + name);
        assertEquals(
                "Attempt to use not existing table must raise error",
                String.format("%s not exists%n", name),
                getOutput());
    }

    @Test
    public void testDropNotExistingTable() throws TerminalException {
        String name = "not_existing_table";
        String command = "drop " + name;
        runBatchExpectNonZero(command);
        assertEquals(
                "Attempt to use not existing table must raise error",
                String.format("%s not exists%n", name),
                getOutput());
    }

    @Test
    public void testCreateTable() throws TerminalException {
        String name = "existing_table";
        String command = "create " + name + " (String)";

        runBatchExpectZero(command);
        assertEquals(
                "Attempt to create not existing table", String.format("created%n"), getOutput());
        runBatchExpectNonZero(command);
        assertEquals(
                "Attempt to create existing table", String.format("%s exists%n", name), getOutput());
    }
}
