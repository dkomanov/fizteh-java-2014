package ru.fizteh.fivt.students.PotapovaSofia.JUnit.Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.PotapovaSofia.JUnit.DbTableProvider;

public class DbTableProviderTest {
    private final Path testFolder = Paths.get(System.getProperty("java.io.tmpdir"), "testFolder");
    private final String dirName = "test";
    private final Path dirPath = testFolder.resolve(dirName);
    private final String testTableName = "testTable";

    @Before
    public void setUp() {
        testFolder.toFile().mkdir();
    }

    @Test
    public void testOnCreatedForNonexistentDirectory() {
        new DbTableProvider(dirPath.toString());
    }

    @Test
    public void testOnCreatedForExistentDirectory() {
        dirPath.toFile().mkdir();
        new DbTableProvider(dirPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnThrowExceptionCreatedNotForDirectory() throws IOException {
        dirPath.toFile().createNewFile();
        new DbTableProvider(dirPath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionCreatedForInvalidPath() {
        new DbTableProvider("\0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnThrowExceptionCreatedForDirectoryWithNondirectoryFile() throws IOException {
        dirPath.toFile().mkdir();
        dirPath.resolve("fileName").toFile().createNewFile();
        new DbTableProvider(dirPath.toString());
    }

    @Test
    public void testOnCreatedForDirectoryContainedDirectory() throws IOException {
        dirPath.toFile().mkdir();
        dirPath.resolve(testTableName).toFile().mkdir();
        TableProvider test = new DbTableProvider(dirPath.toString());
        assertFalse(test.getTable(testTableName) == null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForNullTableName() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.createTable("..");
    }

    @Test
    public void testCreateTableCalledForNewTable() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        assertFalse(test.createTable(testTableName) == null);
        assertTrue(dirPath.resolve(testTableName).toFile().exists());
    }

    @Test
    public void testCreateTableCalledForExistentOnDiskTable() {
        dirPath.resolve(testTableName).toFile().mkdirs();
        TableProvider test = new DbTableProvider(dirPath.toString());
        assertEquals(null, test.createTable(testTableName));
        assertTrue(dirPath.resolve(testTableName).toFile().exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForWrongTableName() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.getTable("ab/cd");
    }

    @Test
    public void testGetTableCalledForNonexistentTable() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        assertEquals(null, test.getTable(testTableName));
    }

    @Test
    public void testGetTableCalledForExistentTable() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        assertFalse(test.createTable(testTableName) == null);
        assertFalse(test.getTable(testTableName) == null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForWrongTableName() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.removeTable("ab\\cd");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThrowsExceptionCalledForNonexistentTable() {
        TableProvider test = new DbTableProvider(dirPath.toString());
        test.removeTable(testTableName);
    }

    @Test
    public void testRemoveTableCalledForExistentFullTable() throws IOException {
        TableProvider test = new DbTableProvider(dirPath.toString());
        assertFalse(test.createTable(testTableName) == null);
        Table testTable = test.getTable(testTableName);
        assertFalse(testTable == null);
        assertEquals(null, testTable.put("key", "value"));
        assertEquals(null, testTable.put("key2", "value"));
        testTable.commit();
        test.removeTable(testTableName);
    }

    @After
    public void tearDown() {
        for (File curFile : testFolder.toFile().listFiles()) {
            if (curFile.isDirectory()) {
                for (File subFile : curFile.listFiles()) {
                    subFile.delete();
                }
            }
            curFile.delete();
        }
        testFolder.toFile().delete();
    }
}
