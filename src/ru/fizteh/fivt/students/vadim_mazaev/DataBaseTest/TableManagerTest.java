package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

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
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManager;

public class TableManagerTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    private final String dirName = "test";
    private final Path dirPath = testDir.resolve(dirName);
    private final String testTableName = "testTable";
    
    @Before
    public void setUp() {
        testDir.toFile().mkdir();
    }

    @Test
    public void testTableManagerCreatedForNonexistentDirectory() {
        new TableManager(dirPath.toString());
    }
    
    @Test
    public void testTableManagerCreatedForExistentDirectory() {
        dirPath.toFile().mkdir();
        new TableManager(dirPath.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedNotForDirectory() throws IOException {
        dirPath.toFile().createNewFile();
        new TableManager(dirPath.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedForInvalidPath() {
        new TableManager("\0");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTableManagerThrowsExceptionCreatedForDirectoryWithNondirectoryFile()
            throws IOException {
        dirPath.toFile().mkdir();
        dirPath.resolve("fileName").toFile().createNewFile();
        new TableManager(dirPath.toString());
    }
    
    @Test
    public void testTableManagerCreatedForDirectoryContainedDirectory()
            throws IOException {
        dirPath.toFile().mkdir();
        dirPath.resolve(testTableName).toFile().mkdir();
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.getTable(testTableName));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForNullTableName() {
        TableProvider test = new TableManager(dirPath.toString());
        test.createTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableThrowsExceptionCalledForWrongTableName() {
        TableProvider test = new TableManager(dirPath.toString());
        //Wrong table name contains '.', '/' or '\'.
        test.createTable("..");
    }
    
    @Test
    public void testCreateTableCalledForNewTable() {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName));
        assertTrue(dirPath.resolve(testTableName).toFile().exists());
    }
    
    @Test
    public void testCreateTableCalledForExistentOnDiskTable() {
        dirPath.resolve(testTableName).toFile().mkdirs();
        TableProvider test = new TableManager(dirPath.toString());
        assertEquals(null, test.createTable(testTableName));
        assertTrue(dirPath.resolve(testTableName).toFile().exists());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new TableManager(dirPath.toString());
        test.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetTableThrowsExceptionCalledForWrongTableName() {
        TableProvider test = new TableManager(dirPath.toString());
        //Wrong table name contains '.', '/' or '\'.
        test.getTable("ab/cd");
    }
    
    @Test
    public void testGetTableCalledForNonexistentTable() {
        TableProvider test = new TableManager(dirPath.toString());
        assertEquals(null, test.getTable(testTableName));
    }
    
    @Test
    public void testGetTableCalledForExistentTable() {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName));
        assertNotEquals(null, test.getTable(testTableName));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForNullTableName() throws Exception {
        TableProvider test = new TableManager(dirPath.toString());
        test.removeTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableThrowsExceptionCalledForWrongTableName() {
        TableProvider test = new TableManager(dirPath.toString());
        //Wrong table name contains '.', '/' or '\'.
        test.removeTable("ab\\cd");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveTableThrowsExceptionCalledForNonexistentTable() {
        TableProvider test = new TableManager(dirPath.toString());
        test.removeTable(testTableName);
    }
    
    @Test
    public void testRemoveTableCalledForExistentFullTable() {
        TableProvider test = new TableManager(dirPath.toString());
        assertNotEquals(null, test.createTable(testTableName));
        Table testTable = test.getTable(testTableName);
        assertNotEquals(null, testTable);
        assertEquals(null, testTable.put("key", "value"));
        assertEquals(null, testTable.put("key2", "value"));
        testTable.commit();
        test.removeTable(testTableName);
    }
    
    @After
    public void tearDown() {
        for (File curFile : testDir.toFile().listFiles()) {
            if (curFile.isDirectory()) {
                for (File subFile : curFile.listFiles()) {
                    subFile.delete();
                }
            }
            curFile.delete();
        }
        testDir.toFile().delete();
    }
}
