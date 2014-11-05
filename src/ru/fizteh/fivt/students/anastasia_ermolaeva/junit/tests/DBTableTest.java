package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.DatabaseIOException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DBTableTest {
    private final Path testDirectory
            = Paths.get(System.getProperty("fizteh.db.dir"));
    private final String tableName = "Таблица1";
    private final Path tableDirectoryPath = testDirectory.resolve(tableName);
    private final String testKey1 = "ключ1";
    private final String testKey2 = "ключ2";
    private final String testValue1 = "значение1";
    private final String testValue2 = "значение2";
    private final String testFile = "Тестовый файл.txt";
    private final String validSubdirectory = "1.dir";
    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;

    @Before
    public final void setUp() {
        testDirectory.toFile().mkdir();
    }

    //Tests on wrong table format.
    @Test(expected = DatabaseIOException.class)
    public final void testDBTableCreatedFromDirectoryWithNonDirectories()
            throws IOException {
        tableDirectoryPath.toFile().mkdir();
        Path newFilePath = tableDirectoryPath.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DBTable(testDirectory, tableName);
    }

    @Test(expected = DatabaseIOException.class)
    public final void testDBTableCreatedFromDirWithWrongSubdirectories() {
        tableDirectoryPath.toFile().mkdir();
        Path newSubdirectory = tableDirectoryPath.resolve("subdirectory");
        newSubdirectory.toFile().mkdir();
        new DBTable(testDirectory, tableName);
    }

    @Test(expected = DatabaseIOException.class)
    public final void testDBTableCreatedFromDirWithEmptySubdirs() {
        tableDirectoryPath.toFile().mkdir();
        Path newSubdirectory = tableDirectoryPath.resolve(validSubdirectory);
        newSubdirectory.toFile().mkdir();
        new DBTable(testDirectory, tableName);
    }

    @Test(expected = DatabaseIOException.class)
    public final void testDBTableCreatedFromDirWithSubdirsWithWrongFiles()
            throws IOException {
        tableDirectoryPath.toFile().mkdir();
        Path newSubdirectory = tableDirectoryPath.resolve(validSubdirectory);
        newSubdirectory.toFile().mkdir();
        Path newFilePath = newSubdirectory.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DBTable(testDirectory, tableName);
    }

    //GetTests.
    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForComittedKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.get(testKey1));
    }

    //PutTests.
    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsIllegalArgumentExceptionCalledForNullKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsExceptionCalledForNullValue() {
        tableDirectoryPath.toFile().mkdir();
        DBTable test = new DBTable(testDirectory, tableName);
        test.put(testKey1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet() {
        tableDirectoryPath.toFile().mkdir();
        DBTable test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        test.put(testKey1, testValue1);
        assertEquals(testValue1, test.put(testKey1, testValue2));
    }

    //RemoveTests.
    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveThrowsExceptionCalledForNullKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        test.remove(null);
    }

    @Test
    public final void testRemoveReturnsNullIfKeyIsNotFound() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit() {
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
    public final void testCommitCreatesRealFileOnTheDisk()
            throws UnsupportedEncodingException {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes("UTF-8")[0]
                % DIR_AMOUNT) + ".dir";
        String fileName = Math.abs((testKey1.getBytes("UTF-8")[0]
                / DIR_AMOUNT) % FILES_AMOUNT)
                + ".dat";
        Path filePath = Paths.get(testDirectory.toString(),
                test.getName(),
                subdirectoryName, fileName);
        assertTrue(filePath.toFile().exists());
    }

    @Test
    public final void testCommitOverwritesCommitedKey() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.put(testKey1, testValue2));
        test.commit();
        assertEquals(testValue2, test.get(testKey1));
    }

    @Test
    public final void testCommitRemovesExistentKeyAfterCommit() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testCommitEmptiedAfterLoadingTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes()[0]
                % DIR_AMOUNT) + ".dir";
        String fileName = Math.abs((testKey1.getBytes()[0]
                / DIR_AMOUNT) % FILES_AMOUNT)
                + ".dat";
        Path filePath = Paths.get(testDirectory.toString(),
                test.getName(),
                subdirectoryName, fileName);
        assertFalse(filePath.toFile().exists());
    }

    @Test
    public final void testCommitReturnsNonZeroChangesPuttingNewRecord() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.put(testKey1, testValue2));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
    }

    //RollbackTests.
    @Test
    public final void testRollbackAfterPuttingNewKey() {
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
    public final void testRollbackNoChanges() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
    }

    //List tests.
    @Test
    public final void testListCalledForEmptyTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable() {
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
    public final void testListCalledForNonEmptyCommitedTable() {
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
    public final void testSizeCalledForNonEmptyNonCommitedTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable() {
        tableDirectoryPath.toFile().mkdir();
        Table test = new DBTable(testDirectory, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @After
    public final void tearDown() throws IOException {
        Files.walkFileTree(testDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e)
                    throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                        /*
                         * directory iteration failed
                          */
                    throw e;
                }
            }
        });
    }
}
