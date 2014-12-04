package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.StoreableTableImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel.ControllableAgent;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel.ControllableRunnable;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.parallel.ControllableRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TableTest extends TestBase {
    private static final List<Class<?>> DEFAULT_COLUMN_TYPES = Arrays.asList(String.class);
    private static final String TABLE_NAME = "table";
    private static TableProviderFactory factory;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private Table table;
    private TableProvider provider;

    @BeforeClass
    public static void globalPrepare() {
        factory = TestUtils.obtainFactory();
    }

    @Before
    public void prepare() throws IOException {
        provider = factory.create(DB_ROOT.toString());
        table = provider.createTable(TABLE_NAME, DEFAULT_COLUMN_TYPES);
    }

    @After
    public void cleanup() throws IOException {
        provider = null;
        table = null;
        cleanDBRoot();
    }

    private String extractString(String value) {
        if (value == null) {
            return null;
        }
        int leftBracket = value.indexOf("\"");
        int rightBracket = value.lastIndexOf("\"");

        return value.substring(leftBracket + 1, rightBracket);
    }

    private String get(String key) {
        Storeable valueObj = table.get(key);
        return valueObj == null ? null : extractString(provider.serialize(table, valueObj));
    }

    private String put(String key, String value) throws ParseException {
        Storeable oldValueObj = table.put(key, provider.deserialize(table, "[\"" + value + "\"]"));
        String oldValue = oldValueObj == null ? null : provider.serialize(table, oldValueObj);
        return extractString(oldValue);
    }

    private String remove(String key) {
        Storeable oldValueObj = table.remove(key);
        String oldValue = oldValueObj == null ? null : provider.serialize(table, oldValueObj);
        return extractString(oldValue);
    }

    @Test
    public void testToString() {
        assertEquals("StoreableTableImpl[" + DB_ROOT.resolve(TABLE_NAME).normalize() + "]", table.toString());
    }

    @Test
    public void testPutWithNullKey() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key must not be null");

        table.put(null, provider.createFor(table));
    }

    @Test
    public void testPutWithNullValue() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Value must not be null");

        table.put("key", null);
    }

    @Test
    public void testOpenTableWithValuesNotMatchingTypes() throws IOException, ParseException {
        put("key", "value");
        table.commit();

        try (PrintWriter writer = new PrintWriter(
                DB_ROOT.resolve(TABLE_NAME).resolve("signature.tsv").toString())) {
            writer.print("int int int");
        }

        provider = factory.create(DB_ROOT.toString());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(containsString("Value of improper format found"));

        table = provider.getTable(TABLE_NAME);
    }

    @Test
    public void testNumberOfUncommittedChanges() throws ParseException {
        put("key", "value");
        put("key2", "value2");

        assertEquals(2, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges1() throws ParseException {
        put("key", "value");
        put("key", "value2");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges2() throws ParseException {
        put("key", "value");
        remove("key");

        assertEquals(0, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges3() throws ParseException, IOException {
        put("key", "value");
        table.commit();
        remove("key");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testNumberOfUncommittedChanges4() throws ParseException, IOException {
        put("key", "value");
        table.commit();
        remove("key");
        put("key", "value");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testRollbackConcurrent() throws Throwable {
        ControllableRunner runnerA = new ControllableRunner();
        ControllableRunner runnerB = new ControllableRunner();

        // Scheme:
        // runnerA: put values
        // runnerB: put values, rollback
        // runnerA: check own changes still exist

        ControllableRunnable contrA = runnerA.createAndAssign(
                (ControllableAgent agent) -> {
                    // runnerA: put values
                    put("a", "b");
                    put("b", "c");

                    agent.notifyAndWait();

                    // runnerA: check own changes still exist
                    int changes = table.getNumberOfUncommittedChanges();
                    assertEquals("Changes of another thread must not have been rolled back", 2, changes);

                });

        ControllableRunnable contrB = runnerB.createAndAssign(
                (ControllableAgent agent) -> {
                    // runnerB: put values, rollback
                    put("a", "b");
                    put("c", "d");
                    int changes = table.rollback();
                    assertEquals("Must have rolled back my changes", 2, changes);
                });

        new Thread(runnerA, "runnerA").start();
        new Thread(runnerB, "runnerB").start();

        runnerA.waitUntilPause(); // Wait until pause.
        runnerB.waitUntilEndOfWork(); // Wait until end of work.
        runnerA.continueWork();
        runnerA.waitUntilEndOfWork(); // Wait until end of work.
    }

    @Test
    public void testCommitGlobalUpdate() throws Throwable {
        ControllableRunner runnerA = new ControllableRunner();
        ControllableRunner runnerB = new ControllableRunner();

        ControllableRunnable contrA = runnerA.createAndAssign(
                (ControllableAgent agent) -> {
                    // runnerA: put values
                    put("a", "b");
                    put("b", "c");

                    agent.notifyAndWait();

                    // runnerA: commit
                    //                    System.err.println("A: commit");
                    int changes = table.commit();
                    assertEquals("Must have committed my changes.", 2, changes);

                    agent.notifyAndWait();

                    // runnerA: get values
                    assertNull("Must have been removed.", get("b"));
                    assertEquals("Committed from another thread.", "d", get("c"));
                    assertEquals("Committed from me earlier.", "b", get("a"));
                });

        ControllableRunnable contrB = runnerB.createAndAssign(
                (ControllableAgent agent) -> {
                    // runnerB: get values, put values, remove values
                    assertNull("Must not have been committed.", get("a"));
                    assertNull("Must not have been committed.", get("b"));

                    put("c", "d");
                    remove("b");

                    agent.notifyAndWait();

                    // runnerB: get values, commit
                    assertNull("Committed but replaced", get("b"));
                    assertEquals("Committed from another thread.", "b", get("a"));
                    int changes = table.commit();
                    assertEquals("Must have committed my changes.", 2, changes);
                });

        // runnerA: put values
        // runnerB: get values, put values, remove values
        // runnerA: commit
        // runnerB: get values, commit
        // runnerA: get values

        new Thread(runnerA, "runnerA").start();
        runnerA.waitUntilPause();
        new Thread(runnerB, "runnerB").start();
        runnerB.waitUntilPause();
        runnerA.continueWork();
        runnerA.waitUntilPause();
        runnerB.waitUntilEndOfWork();
        runnerA.waitUntilEndOfWork();
    }

    @Test
    public void testNumberOfUncommittedChanges5() throws ParseException, IOException {
        put("key", "value");
        table.commit();
        remove("key");
        put("key", "value2");

        assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testManyConcurrentCommits() throws Exception {
        int threadsNumber = TestUtils.ALPHABET.length;

        ControllableRunner[] runners = new ControllableRunner[threadsNumber];

        for (int i = 0; i < threadsNumber; i++) {
            final int id = i;
            runners[i] = new ControllableRunner();
            runners[i].createAndAssign(
                    (ControllableAgent agent) -> {
                        final ThreadLocalRandom random = ThreadLocalRandom.current();

                        Consumer<Integer> trashFiller = (actionsCount) -> {
                            try {
                                System.err.println(
                                        Thread.currentThread().getName() + ": doing " + actionsCount
                                        + " actions");
                                for (int j = 0; j < actionsCount; j++) {
                                    put(
                                            String.valueOf(random.nextInt(100)),
                                            String.valueOf(random.nextInt()));
                                    if (random.nextInt(10) < 5) {
                                        table.commit();
                                    }
                                }
                            } catch (IOException | ParseException exc) {
                                throw new AssertionError(exc);
                            }
                        };

                        trashFiller.accept(random.nextInt(20, 40));

                        String mainKey = "key" + TestUtils.ALPHABET[id];
                        String mainValue = "value " + mainKey;

                        put(mainKey, mainValue);
                        table.commit();
                        assertEquals("Main key-value not matches", mainValue, get(mainKey));

                        trashFiller.accept(random.nextInt(20, 40));

                        agent.notifyAndWait();

                        assertEquals("Main key-value not matches", mainValue, get(mainKey));
                    });
        }

        for (int i = 0; i < threadsNumber; i++) {
            new Thread(runners[i], "runner " + i).start();
        }

        // Going to the finish line...
        for (ControllableRunner runner : runners) {
            runner.waitUntilPause();
        }

        // Total check in the end.
        for (ControllableRunner runner : runners) {
            runner.waitUntilEndOfWork();
        }
    }

    @Test
    public void testGetColumnTypeAtBadIndex() {
        exception.expect(IndexOutOfBoundsException.class);

        table.getColumnType(table.getColumnsCount());
    }

    @Test
    public void testGetColumnTypeAtBadIndex1() {
        exception.expect(IndexOutOfBoundsException.class);

        table.getColumnType(-1);
    }

    @Test
    public void testParseUnsupportedColumnType() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                wrongTypeMatcherAndAllOf(containsString("no_such_type not supported")));

        StoreableTableImpl.parseColumnTypes("no_such_type");
    }

    @Test
    public void testGetExistent() throws ParseException {
        put("key", "value");
        String value = get("key");
        assertEquals("Existent key must return proper value", "value", value);
    }

    @Test
    public void testGetNotExistent() {
        String value = get("not_existent");
        assertNull("Not existent key must give null value", value);
    }

    @Test
    public void testGetNullKey() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key must not be null");
        get(null);
    }

    @Test
    public void testPutNew() throws ParseException {
        int sizeBefore = table.size();
        String oldValue = put("new", "value");
        int sizeAfter = table.size();

        assertNull("When inserting new (key,value), old value must be null", oldValue);
        assertEquals("WHen inserting new key, size must increase by 1", sizeBefore + 1, sizeAfter);
    }

    @Test
    public void testPutExistent() throws ParseException {
        put("existent", "value1");
        int sizeBefore = table.size();
        String oldValue = put("existent", "value2");
        int sizeAfter = table.size();

        assertEquals("Wrong old value when putting existent key", "value1", oldValue);
        assertEquals("When putting existent key, size must not change", sizeBefore, sizeAfter);
    }

    @Test
    public void testRemoveNotExistent() {
        int sizeBefore = table.size();
        String oldValue = remove("not existent");
        int sizeAfter = table.size();

        assertNull("When removing not existent key, old value must be null", oldValue);
        assertEquals("When removing not existent key, size must not change", sizeBefore, sizeAfter);
    }

    @Test
    public void testRussianSymbols() throws ParseException {
        put("ключ", "значение");

        assertEquals("Russian symbols: put + get not synchronized", "значение", get("ключ"));
    }

    @Test
    public void testRemoveExistent() throws ParseException {
        put("key", "value1");
        int sizeBefore = table.size();
        String oldValue = remove("key");
        int sizeAfter = table.size();

        assertEquals("When removing existent key, old value must be returned", "value1", oldValue);
        assertEquals(
                "When removing existent key, size must decrease by one", sizeBefore - 1, sizeAfter);
    }
}
