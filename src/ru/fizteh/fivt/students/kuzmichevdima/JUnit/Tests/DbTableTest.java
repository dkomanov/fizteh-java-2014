package ru.fizteh.fivt.students.kuzmichevdima.JUnit.Tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kuzmichevdima.JUnit.DbTable;
import ru.fizteh.fivt.students.kuzmichevdima.JUnit.Interpreter.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DbTableTest {
    private final Path testFolder = Paths.get(System.getProperty("java.io.tmpdir"), "testFolder");
    private final String tableName = "testTable";
    private final Path tableDirectoryPath = testFolder.resolve(tableName);
    private final String testKey1 = "k1";
    private final String testKey2 = "k2";
    private final String testValue1 = "v1";
    private final String testValue2 = "v2";
    private final String testFile = "file.txt";
    private final String validDir = "1.dir";
    private static final int DIR_COUNT = 16;
    private static final int FILE_COUNT = 16;

    @Before
    public final void setUp() throws IOException {
        if (!testFolder.toFile().exists()) {
            Files.createDirectory(testFolder);
        }
        if (!tableDirectoryPath.toFile().exists()) {
            Files.createDirectory(tableDirectoryPath);
        }
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirectoryWithNonDirectories() throws IOException {
        Path newFilePath = tableDirectoryPath.resolve(testFile);
        if (!newFilePath.toFile().exists()) {
            newFilePath.toFile().createNewFile();
        }
        try {
            new DbTable(tableDirectoryPath, tableName);
        } catch (RuntimeException e) {
            newFilePath.toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirWithWrongSubdirectories() throws IOException {
        Path newDir = tableDirectoryPath.resolve("subdirectory");
        Files.createDirectory(newDir);
        new DbTable(tableDirectoryPath, tableName);
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirWithEmptySubdirs() throws IOException {
        Path newDir = tableDirectoryPath.resolve(validDir);
        Files.createDirectory(newDir);
        new DbTable(tableDirectoryPath, tableName);
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirWithSubdirsWithWrongFiles() throws IOException {
        Path newDir = tableDirectoryPath.resolve(validDir);
        Files.createDirectory(newDir);
        Path newFilePath = newDir.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DbTable(tableDirectoryPath, tableName);
    }

    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() throws IOException {
        Table test = new DbTable(testFolder.resolve(tableName), tableName);
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForComittedKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.get(testKey1));
    }


    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsIllegalArgumentExceptionCalledForNullKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsExceptionCalledForNullValue() throws IOException {
        DbTable test = new DbTable(tableDirectoryPath, tableName);
        test.put(testKey1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet() throws IOException {
        DbTable test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        test.put(testKey1, testValue1);
        assertEquals(testValue1, test.put(testKey1, testValue2));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveThrowsExceptionCalledForNullKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        test.remove(null);
    }

    @Test
    public final void testRemoveReturnsNullIfKeyIsNotFound() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testCommitCreatesRealFileOnTheDisk() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        Integer hashCode = Math.abs(testKey1.hashCode());
        Integer dir = hashCode % DIR_COUNT;
        Integer file = hashCode / DIR_COUNT % FILE_COUNT;
        String dirName = dir + ".dir";
        String fileName = file + ".dat";
        Path filePath = tableDirectoryPath.resolve(dirName).resolve(fileName);
        assertTrue(filePath.toFile().exists());
    }

    @Test
    public final void testCommitOverwritesCommitedKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.put(testKey1, testValue2));
        test.commit();
        assertEquals(testValue2, test.get(testKey1));
    }

    @Test
    public final void testCommitRemovesExistentKeyAfterCommit() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testCommitEmptiedAfterLoadingTable() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        Integer hashCode = Math.abs(testKey1.hashCode());
        Integer dir = hashCode % DIR_COUNT;
        Integer file = hashCode / DIR_COUNT % FILE_COUNT;
        String dirName = dir + ".dir";
        String fileName = file + ".dat";
        Path filePath = tableDirectoryPath.resolve(dirName).resolve(fileName);
        assertTrue(!filePath.toFile().exists());
    }

    @Test
    public final void testCommitReturnsNonZeroChangesPuttingNewRecord() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.put(testKey1, testValue2));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
    }

    @Test
    public final void testRollbackAfterPuttingNewKey() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertEquals(0, test.size());
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.size());
        assertEquals(1, test.rollback());
        assertEquals(0, test.size());
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testRollbackNoChanges() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
    }

    @Test
    public final void testListCalledForEmptyTable() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
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
    public final void testListCalledForNonEmptyCommitedTable() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
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

    @Test
    public final void testSizeCalledForNonEmptyNonCommitedTable() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable() throws IOException {
        Table test = new DbTable(tableDirectoryPath, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @After
    public final void tearDown() throws IOException {
        Utils.recursiveDelete(testFolder);
    }
}
