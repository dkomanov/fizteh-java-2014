package ru.fizteh.fivt.students.LebedevAleksey.junit.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.LebedevAleksey.junit.AdditionalAssert;
import ru.fizteh.fivt.students.LebedevAleksey.junit.StringTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class StringTableTest {

    public static final String TEST_TABLE_NAME = "TestTable";
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();
    private static File dbPath;
    private static TableProvider database;
    private Table table;

    @BeforeClass
    public static void setUpClass() throws IOException {
        dbPath = folder.newFolder("db");
        database = new StringTableProviderFactory().create(dbPath.getPath());
    }

    @Before
    public void setUp() throws Exception {
        Table tableInstance = database.getTable(TEST_TABLE_NAME);
        if (tableInstance != null) {
            database.removeTable(TEST_TABLE_NAME);
        }
        table = database.createTable(TEST_TABLE_NAME);
    }

    @After
    public void tearDown() throws Exception {
        database.removeTable(TEST_TABLE_NAME);
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(TEST_TABLE_NAME, table.getName());
        Assert.assertEquals("Qwerty", database.createTable("Qwerty").getName());
        database.removeTable("Qwerty");
    }

    @Test
    public void testGetAndPutCommands() throws Exception {
        Assert.assertEquals(null, table.get("a"));
        Assert.assertEquals(null, table.get("b"));
        Assert.assertEquals(null, table.get("c"));
        Assert.assertEquals(null, table.get("d"));

        Assert.assertEquals(0, table.list().size());
        Assert.assertEquals(null, table.put("a", "b"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a"}, table.list());

        Assert.assertEquals("b", table.get("a"));
        Assert.assertEquals(null, table.get("b"));
        Assert.assertEquals(null, table.get("c"));
        Assert.assertEquals(null, table.get("d"));

        AdditionalAssert.assertArrayAndListEquals(new String[]{"a"}, table.list());
        Assert.assertEquals(null, table.put("c", "d"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());

        Assert.assertEquals("b", table.get("a"));
        Assert.assertEquals(null, table.get("b"));
        Assert.assertEquals("d", table.get("c"));
        Assert.assertEquals(null, table.get("d"));

        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());
        Assert.assertEquals("b", table.put("a", "d"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());

        Assert.assertEquals("d", table.get("a"));
        Assert.assertEquals(null, table.get("b"));
        Assert.assertEquals("d", table.get("c"));
        Assert.assertEquals(null, table.get("d"));

        Assert.assertEquals(null, table.get("qwerty"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());
        Assert.assertEquals(null, table.put("qwerty", "asdfg"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c", "qwerty"}, table.list());
        Assert.assertEquals("asdfg", table.get("qwerty"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c", "qwerty"}, table.list());
        Assert.assertEquals("asdfg", table.put("qwerty", "test"));
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c", "qwerty"}, table.list());
        Assert.assertEquals("test", table.get("qwerty"));
    }

    @Test
    public void testRemoveAndListAndSizeCommands() throws Exception {
        Assert.assertEquals(0, table.list().size());
        Assert.assertEquals(0, table.size());

        table.put("a", "ab");
        table.put("b", "bb");
        table.put("c", "cb");

        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "b", "c"}, table.list());

        Assert.assertEquals(null, table.remove("d"));

        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "b", "c"}, table.list());

        Assert.assertEquals("bb", table.remove("b"));

        Assert.assertEquals(2, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c"}, table.list());

        table.put("name", "surname");

        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c", "name"}, table.list());

        Assert.assertEquals("cb", table.remove("c"));

        Assert.assertEquals(2, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "name"}, table.list());
        Assert.assertEquals("ab", table.get("a"));
        Assert.assertEquals("surname", table.get("name"));

        Assert.assertEquals("surname", table.remove("name"));

        Assert.assertEquals(1, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a"}, table.list());
        Assert.assertEquals("ab", table.get("a"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectArgument() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectArgument() {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutFirstIncorrectArgument() {
        table.put(null, "a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutSecondIncorrectArgument() {
        table.put("a", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutBothIncorrectArguments() {
        table.put(null, null);
    }

    @Test
    public void testIncorrectPutDoesntChangeAnything() {
        checkEmpty();
        try {
            table.put("a", null);
            Assert.fail("Should be exception");
        } catch (IllegalArgumentException e) {
            //We are waiting this exception
        }
        checkEmpty();
        try {
            table.put(null, "a");
            Assert.fail("Should be exception");
        } catch (IllegalArgumentException e) {
            //We are waiting this exception
        }
        checkEmpty();
        try {
            table.put(null, null);
            Assert.fail("Should be exception");
        } catch (IllegalArgumentException e) {
            //We are waiting this exception
        }
        checkEmpty();
        try {
            table.get(null);
            Assert.fail("Should be exception");
        } catch (IllegalArgumentException e) {
            //We are waiting this exception
        }
        checkEmpty();
        try {
            table.remove(null);
            Assert.fail("Should be exception");
        } catch (IllegalArgumentException e) {
            //We are waiting this exception
        }
        checkEmpty();
        table.put("a", "a");
        Assert.assertEquals(1, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a"}, table.list());
        Assert.assertEquals("a", table.get("a"));
    }

    private void checkEmpty() {
        Assert.assertEquals(0, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[0], table.list());
        Assert.assertEquals(null, table.get("a"));
    }

    private void make1Change1() {
        table.put("a", "a");
    }

    private void make1ChangeRemove() {
        table.remove("a");
    }

    private void make2Change() {
        table.put("a", "a");
        table.put("b", "b");
        table.put("c", "c");
        table.remove("c");
    }


    private void make3Change() {
        table.put("a", "ab");
        table.put("b", "bb");
        table.put("c", "cb");
    }

    private void make4Change() {
        table.put("a", "a");
        table.remove("c");
        table.put("qwerty", "1234567");
        table.put("qw", "12");
    }

    @Test
    public void testCommitCounts() throws Exception {
        make1Change1();
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals(0, table.commit());
        make1ChangeRemove();
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals(0, table.commit());
        make2Change();
        Assert.assertEquals(2, table.rollback());
        make2Change();
        Assert.assertEquals(2, table.commit());
        make3Change();
        Assert.assertEquals(3, table.rollback());
        make3Change();
        Assert.assertEquals(3, table.commit());
        make3Change();
        Assert.assertEquals(0, table.rollback());
        make4Change();
        Assert.assertEquals(4, table.rollback());
    }

    @Test
    public void testRollbackCount() throws Exception {
        make1Change1();
        Assert.assertEquals(1, table.rollback());
        Assert.assertEquals(0, table.rollback());
        make1ChangeRemove();
        Assert.assertEquals(0, table.rollback());
        Assert.assertEquals(0, table.rollback());
        make2Change();
        Assert.assertEquals(2, table.commit());
        make3Change();
        Assert.assertEquals(3, table.commit());
        make3Change();
        Assert.assertEquals(0, table.commit());
        make4Change();
        Assert.assertEquals(4, table.commit());
    }

    private boolean hasFlag(int position, int value) {
        return (((value >> position) % 2) == 1);
    }


    @Test
    public void testCommit() {
        for (int i = 0; i < 1 << 5; i++) {
            database.removeTable(TEST_TABLE_NAME);
            table = database.createTable(TEST_TABLE_NAME);
            Assert.assertEquals(null, table.get("a"));
            Assert.assertEquals(null, table.get("b"));
            Assert.assertEquals(null, table.get("c"));
            Assert.assertEquals(null, table.get("d"));

            Assert.assertEquals(0, table.list().size());
            Assert.assertEquals(null, table.put("a", "b"));
            checkCommit(i, 0);
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a"}, table.list());

            Assert.assertEquals("b", table.get("a"));
            Assert.assertEquals(null, table.get("b"));
            Assert.assertEquals(null, table.get("c"));
            Assert.assertEquals(null, table.get("d"));

            AdditionalAssert.assertArrayAndListEquals(new String[]{"a"}, table.list());
            Assert.assertEquals(null, table.put("c", "d"));
            checkCommit(i, 1);
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());

            Assert.assertEquals("b", table.get("a"));
            Assert.assertEquals(null, table.get("b"));
            Assert.assertEquals("d", table.get("c"));
            Assert.assertEquals(null, table.get("d"));

            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());
            Assert.assertEquals("b", table.put("a", "d"));
            checkCommit(i, 2);
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());

            Assert.assertEquals("d", table.get("a"));
            Assert.assertEquals(null, table.get("b"));
            Assert.assertEquals("d", table.get("c"));
            Assert.assertEquals(null, table.get("d"));

            Assert.assertEquals(null, table.get("qwerty"));
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c"}, table.list());
            Assert.assertEquals(null, table.put("qwerty", "asdfg"));
            checkCommit(i, 3);
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c", "qwerty"}, table.list());
            Assert.assertEquals("asdfg", table.get("qwerty"));
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c", "qwerty"}, table.list());
            Assert.assertEquals("asdfg", table.put("qwerty", "test"));
            checkCommit(i, 4);
            AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "c", "qwerty"}, table.list());
            Assert.assertEquals("test", table.get("qwerty"));
        }
        for (int i = 0; i < 1 << 6; i++) {
            database.removeTable(TEST_TABLE_NAME);
            table = database.createTable(TEST_TABLE_NAME);

            Assert.assertEquals(0, table.list().size());
            Assert.assertEquals(0, table.size());
            table.put("a", "ab");
            table.put("b", "bb");
            table.put("c", "cb");
            checkCommit(i, 0);

            Assert.assertEquals(3, table.size());
            AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "b", "c"}, table.list());

            Assert.assertEquals(null, table.remove("d"));
            checkCommit(i, 1);

            Assert.assertEquals(3, table.size());
            AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "b", "c"}, table.list());

            Assert.assertEquals("bb", table.remove("b"));
            checkCommit(i, 2);

            Assert.assertEquals(2, table.size());
            AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c"}, table.list());

            table.put("name", "surname");
            checkCommit(i, 3);

            Assert.assertEquals(3, table.size());
            AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c", "name"}, table.list());

            Assert.assertEquals("cb", table.remove("c"));
            checkCommit(i, 4);

            Assert.assertEquals(2, table.size());
            AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "name"}, table.list());
            Assert.assertEquals("ab", table.get("a"));
            Assert.assertEquals("surname", table.get("name"));

            Assert.assertEquals("surname", table.remove("name"));
            checkCommit(i, 5);

            Assert.assertEquals(1, table.size());
            AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a"}, table.list());
            Assert.assertEquals("ab", table.get("a"));
        }
    }

    private void checkCommit(int value, int position) {
        if (hasFlag(position, value)) {
            checkCommit();
        }
    }

    private int checkCommit() {
        int changes = table.commit();
        table = database.getTable(TEST_TABLE_NAME);
        Path tablePath = dbPath.toPath().resolve(TEST_TABLE_NAME);
        File[] directories = tablePath.toFile().listFiles();
        for (File dir : directories) {
            File[] files = dir.listFiles();
            Assert.assertTrue(files.length > 0);
            for (File file : files) {
                Assert.assertTrue(file.length() > 0);
            }
        }
        return changes;
    }

    @Test
    public void testCommitWithCollisions() {
        table.put("Q1", "text");
        table.put("r2", "text");
        Assert.assertEquals(2, checkCommit());
        Assert.assertEquals(2, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"Q1", "r2"}, table.list());
        table.remove("Q1");
        table.put("test", "text");
        Assert.assertEquals(2, checkCommit());
        Assert.assertEquals(2, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"test", "r2"}, table.list());
        table.put("Q1", "text");
        table.put("r2", "text");
        Assert.assertEquals(1, checkCommit());
        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"Q1", "r2", "test"}, table.list());
        table.remove("Q1");
        table.remove("r2");
        Assert.assertEquals(2, checkCommit());
        Assert.assertEquals(1, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"test"}, table.list());
    }

    @Test
    public void testRollback() {
        table.put("a", "a");
        table.put("b", "b");
        table.put("c", "c");
        table.commit();
        Assert.assertEquals("a", table.get("a"));
        Assert.assertEquals("b", table.get("b"));
        Assert.assertEquals("c", table.get("c"));
        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c", "b"}, table.list());
        table.put("a", "e");
        table.remove("b");
        table.put("d", "d");
        Assert.assertEquals("e", table.get("a"));
        Assert.assertEquals("c", table.get("c"));
        Assert.assertEquals("d", table.get("d"));
        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c", "d"}, table.list());
        table.rollback();
        Assert.assertEquals("a", table.get("a"));
        Assert.assertEquals("b", table.get("b"));
        Assert.assertEquals("c", table.get("c"));
        Assert.assertEquals(3, table.size());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a", "c", "b"}, table.list());
    }
}
