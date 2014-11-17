package ru.fizteh.fivt.students.LebedevAleksey.junit.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.*;
import ru.fizteh.fivt.students.LebedevAleksey.junit.AdditionalAssert;
import ru.fizteh.fivt.students.LebedevAleksey.junit.Database;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DatabaseTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();
    private static File dbPath;
    private static Database database;

    @BeforeClass
    public static void setUpClass() throws IOException, DatabaseFileStructureException, LoadOrSaveException {
        dbPath = folder.newFolder("db");
        database = new Database(dbPath.getPath());
    }

    @Test
    public void testGetAndCreateTable() throws
            LoadOrSaveException, DatabaseFileStructureException, TableAlreadyExistsException, TableNotFoundException {
        assertTableNotExsist("abc");
        Table table = (Table) database.createTable("abc");
        Assert.assertTrue(database.containsTable("abc"));
        table.put("a", "a");
        table.save();
        table = (Table) database.getTable("abc");
        Assert.assertEquals(1, table.count());
        AdditionalAssert.assertStringArrayContainsSameItemsAsList(new String[]{"a"}, table.list());
        assertTableNotExsist("abcd");
        assertTableNotExsist("ab");
        assertTableNotExsist("a");
        assertTableNotExsist("qwerty");
        database.removeTable("abc");
    }

    private void assertTableNotExsist(String name) {
        Assert.assertFalse(database.containsTable(name));
        try {
            database.getTable(name);
            Assert.fail("Table \"" + name + "\" exist!");
        } catch (TableNotFoundException e) {
            // It is ok
        }
    }

    @Test(expected = TableNotFoundException.class)
    public void testExceptionThrowsThanDropNotExistingTable() throws TableNotFoundException,
            DatabaseFileStructureException, LoadOrSaveException {
        database.removeTable("notExist");
    }

    @Test
    public void testCanRemoveTable() throws TableAlreadyExistsException, LoadOrSaveException,
            DatabaseFileStructureException, TableNotFoundException {
        Table table = (Table) database.createTable("name");
        table.put("a", "a");
        table.save();
        database.removeTable("name");
        assertTableNotExsist("name");
    }

    @Test
    public void testCanRemoveTableWithNoCommits() throws TableAlreadyExistsException,
            LoadOrSaveException, DatabaseFileStructureException, TableNotFoundException {
        Table table = (Table) database.createTable("name");
        database.removeTable("name");
        assertTableNotExsist("name");
        table = (Table) database.createTable("name");
        Assert.assertTrue(database.containsTable("name"));
        database.removeTable("name");
        assertTableNotExsist("name");
    }

    @Test
    public void testListTable() throws DatabaseFileStructureException, LoadOrSaveException,
            TableAlreadyExistsException, TableNotFoundException {
        Assert.assertEquals(0, database.listTables().size());
        Table table = (Table) database.createTable("a");
        database.createTable("b");
        database.removeTable("b");
        List<Pair<String, Integer>> tables = database.listTables();
        Assert.assertEquals(1, tables.size());
        Assert.assertEquals("a", tables.get(0).getKey());
        Assert.assertEquals(0, (int) tables.get(0).getValue());
        table.put("1", "1");
        table.put("2", "2");
        table.put("2", "2");
        tables = database.listTables();
        Assert.assertEquals(1, tables.size());
        Assert.assertEquals("a", tables.get(0).getKey());
        Assert.assertEquals(2, (int) tables.get(0).getValue());
        table.save();
        tables = database.listTables();
        Assert.assertEquals(1, tables.size());
        Assert.assertEquals("a", tables.get(0).getKey());
        Assert.assertEquals(2, (int) tables.get(0).getValue());
        database.removeTable("a");
    }

    @Test
    public void test() throws TableNotFoundException, DatabaseFileStructureException,
            LoadOrSaveException, TableAlreadyExistsException {
        database.createTable("qwerty");
        database.createTable("asdf");
        database.useTable("qwerty");
        Assert.assertEquals("qwerty", database.getCurrentTable().getTableName());
        database.useTable("asdf");
        Assert.assertEquals("asdf", database.getCurrentTable().getTableName());
        database.removeTable("qwerty");
        database.removeTable("asdf");
    }

    @Test(expected = TableNotFoundException.class)
    public void testSetCurrentTableNotExsist() throws TableNotFoundException, DatabaseFileStructureException,
            LoadOrSaveException {
        database.useTable("notExsist");
    }

    @Test
    public void testGetTableFromManyTablesAndLoadDatabase() throws TableAlreadyExistsException, LoadOrSaveException,
            DatabaseFileStructureException, TableNotFoundException {
        for (int i = 0; i < 10; i++) {
            database.createTable("t" + i);
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("t" + i, database.getTable("t" + i).getTableName());
        }
        database = new Database(dbPath.getPath());
        for (int i = 0; i < 10; i++) {
            database.removeTable("t" + i);
        }
    }

    @Test
    public void testIncorrectTablename() throws TableAlreadyExistsException, LoadOrSaveException,
            DatabaseFileStructureException {
        Assert.assertEquals(0, database.listTables().size());
        try {
            database.createTable(".." + File.separator + "abc");
            Assert.fail("Wrong table name");
        } catch (DatabaseFileStructureException e) {
            // Normal
        }
        try {
            database.createTable("qwerty" + File.separator + "abc");
            Assert.fail("Wrong table name");
        } catch (DatabaseFileStructureException e) {
            // Normal
        }
        Assert.assertEquals(0, database.listTables().size());
    }

    @Test
    public void testTableAlreadyExistsExceptionThrows() throws TableAlreadyExistsException, LoadOrSaveException,
            DatabaseFileStructureException, TableNotFoundException {
        database.createTable("testName");
        try {
            database.createTable("testName");
            Assert.fail("Second table created");
        } catch (TableAlreadyExistsException e) {
            // Normal
        }
        database.removeTable("testName");
    }

    @Test(expected = DatabaseFileStructureException.class)
    public void testFileInDbFolder() throws IOException, DatabaseFileStructureException, LoadOrSaveException {
        File path = folder.newFolder("newDB");
        folder.newFile("newDB" + File.separator + "file.txt");
        Database db = new Database(path.getPath());
    }
}
