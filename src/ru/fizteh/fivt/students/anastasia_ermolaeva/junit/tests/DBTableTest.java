package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.DBTable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DBTableTest {
    private final Path testDirectory = Paths.get(System.getProperty("fizteh.db.dir"));
    private static final String tableName = "Таблица1";
    private final Path tableDirectoryPath = testDirectory.resolve(tableName);
    private static final String testKey1 = "ключ1";
    private static final String testKey2 = "ключ2";
    private static final String testKey3 = "key";
    private static final String testValue1 = "значение1";
    private static final String testValue2 = "значение2";
    private static final String testValue3 = "value";
    private static final String testFile = "Тестовый файл.txt";
    private static final String validSubdirectory = "1.dir";
    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;

    @Before
    public void setUp() {
        testDirectory.toFile().mkdir();
    }
    //Tests on wrong table format.
    @Test(expected = IllegalStateException.class)
    public void testDBTableCreatedFromDirectoryWithNonDirectoryFiles() throws IOException {
        tableDirectoryPath.toFile().mkdir();
        Path newFilePath = tableDirectoryPath.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DBTable(testDirectory, tableName);
    }

    @Test(expected = IllegalStateException.class)
    public void testDBTableCreatedFromDirectoryWithWrongSubdirectoriesNames() {
        tableDirectoryPath.toFile().mkdir();
        Path newSubdirectory = tableDirectoryPath.resolve("subdirectory");
        newSubdirectory.toFile().mkdir();
        new DBTable(testDirectory, tableName);
    }

    @Test(expected = IllegalStateException.class)
    public void testDBTableCreatedFromDirectoryWithValidButEmptySubdirectories() {
        tableDirectoryPath.toFile().mkdir();
        Path newSubdirectory = tableDirectoryPath.resolve(validSubdirectory);
        newSubdirectory.toFile().mkdir();
        new DBTable(testDirectory, tableName);
    }

    @Test(expected = IllegalStateException.class)
    public void testDBTableCreatedFromDirectoryWithValidSubdirectoriesContainedWrongNameFiles() throws IOException {
        tableDirectoryPath.toFile().mkdir();
        Path newSubdirectory = tableDirectoryPath.resolve(validSubdirectory);
        newSubdirectory.toFile().mkdir();
        Path newFilePath = newSubdirectory.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DBTable(testDirectory, tableName);
    }

    //GetTests.
    @Test
    public void testGetReturnsNullIfKeyIsNotFound() {
        tableDirectoryPath.toFile().mkdir();
        Table test  = new DBTable(testDirectory, tableName);
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsIllegalArgumentExceptionCalledForNullKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test  = new DBTable(testDirectory, tableName);
        test.get(null);
    }

    @Test
    public void testGetCalledForNonComittedKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public void testGetCalledForComittedKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public void testGetCalledForDeletedKeyBeforeCommit() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.get(testKey1));
    }

    //PutTests.
    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsIllegalArgumentExceptionCalledForNullKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test  = new DBTable(testDirectory, tableName);
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsIllegalArgumentExceptionCalledForNullValue() {
        tableDirectoryPath.toFile().mkdir();
        DBTable test  = new DBTable(testDirectory, tableName);
        test.put(testKey1, null);
    }

    @Test
    public void testPutReturnsNullIfKeyHasNotBeenWrittenYet() {
        tableDirectoryPath.toFile().mkdir();
        DBTable test  = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
    }
    @Test
    public void testPutReturnsOldValueIfKeyExists() {
        tableDirectoryPath.toFile().mkdir();
        Table test  = new DBTable(testDirectory, tableName);
        test.put(testKey1, testValue1);
        assertEquals(testValue1, test.put(testKey1, testValue2));
    }

    //RemoveTests.
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveThrowsIllegalArgumentExceptionCalledForNullKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test  = new DBTable(testDirectory, tableName);
        test.remove(null);
    }

    @Test
    public void testRemoveReturnsNullIfKeyIsNotFound() {
        tableDirectoryPath.toFile().mkdir();
        Table test  = new DBTable(testDirectory, tableName);
        assertNull(test.remove(testKey1));
    }

    @Test
    public void testRemoveCalledForDeletedKeyBeforeCommit(){
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.remove(testKey1));
    }

    @Test
    public void testRemoveCalledForDeletedKeyAfterCommit(){
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.remove(testKey1));
    }

    /*CommitTests.
    Also checks implicitly inside put, get and remove tests.
    */
    @Test
    public void testCommitCreatesRealFileOnTheDisk() throws UnsupportedEncodingException {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes("UTF-8")[0] % DIR_AMOUNT) + ".dir";
        String fileName = Math.abs((testKey1.getBytes("UTF-8")[0] / DIR_AMOUNT) % FILES_AMOUNT) + ".dat";
        Path filePath = Paths.get(testDirectory.toString(), test.getName(),subdirectoryName, fileName);
        assertTrue(filePath.toFile().exists());
    }

    @Test
    public void testCommitOverwritesCommitedKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.put(testKey1, testValue2));
        test.commit();
        assertEquals(testValue2, test.get(testKey1));
    }

    @Test
    public void testCommitRemovesExistentKeyAfterCommit() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.get(testKey1));
    }

    @Test
    public void testCommitEmptiedAfterLoadingTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes()[0] % DIR_AMOUNT) + ".dir";
        String fileName = Math.abs((testKey1.getBytes()[0] / DIR_AMOUNT) % FILES_AMOUNT) + ".dat";
        Path filePath = Paths.get(testDirectory.toString(), test.getName(), subdirectoryName, fileName);
        assertFalse(filePath.toFile().exists());
    }

    @Test
    public void testCommitReturnsNonZeroNumberOfChangesAfterPuttingNewRecord() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
    }

    @Test
    public void testCommitReturnsNotZeroNumberOfChangesAfterRewritingRecord() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.put(testKey1, testValue2));
        assertEquals(1, test.commit());
    }

    @Test
    public void testCommitNoChanges(){
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
    }

    //RollbackTests.
    @Test
    public void testRollbackAfterPuttingNewKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertEquals(0, test.size());
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.size());
        assertEquals(1, test.rollback());
        assertEquals(0, test.size());
        assertNull(test.get(testKey1));
    }

    @Test
    public void testRollbackNoChanges() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
    }

    //List tests.
    @Test
    public void testListCalledForEmptyTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public void testListCalledForNonEmptyNewTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey1);
        expectedKeySet.add(testKey2);
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
    }

    @Test
    public void testListCalledForNonEmptyCommitedTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey1);
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
    }

    //Size tests.
    @Test
    public void testSizeCalledForNonEmptyNonCommitedTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public void testSizeCalledForNonEmptyCommitedTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @After
    public void tearDown() throws Exception {
        for (File currentTableDirectory : testDirectory.toFile().listFiles()) {
            if (currentTableDirectory.isDirectory()) {
                for (File tableSubDirectory : currentTableDirectory.listFiles()) {
                    if(tableSubDirectory.isDirectory()){
                        for(File tableFile: tableSubDirectory.listFiles()) {
                            tableFile.delete();
                        }
                    }
                    tableSubDirectory.delete();
                }
            }
            currentTableDirectory.delete();
        }
        testDirectory.toFile().delete();
    }
}
