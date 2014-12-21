package ru.fizteh.fivt.students.PotapovaSofia.storeable.Tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.DbTable;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.DbTableProvider;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.StoreableImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private final String testFile = "file.txt";
    private final String validDir = "1.dir";
    private static final int DIR_COUNT = 16;
    private static final int FILE_COUNT = 16;
    private static List<Class<?>> types;
    private static Storeable testValue1;
    private static Storeable testValue2;
    private TableProvider provider;
    private Table test;

    @Before
    public final void setUp() throws IOException {
        if (!testFolder.toFile().exists()) {
            Files.createDirectory(testFolder);
        }
        provider = new DbTableProvider(testFolder.toString());
        types = new ArrayList<>();
        types.add(Integer.class);
        testValue1 = new StoreableImpl(types);
        testValue1.setColumnAt(0, 1);
        testValue2 = new StoreableImpl(types);
        testValue2.setColumnAt(0, 5);
        test = provider.createTable(tableName, types);
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirectoryWithNonDirectories() throws IOException {
        if (!tableDirectoryPath.toFile().exists()) {
            Files.createDirectory(tableDirectoryPath);
        }
        Path newFilePath = tableDirectoryPath.resolve(testFile);
        if (!newFilePath.toFile().exists()) {
            newFilePath.toFile().createNewFile();
        }
        try {
            new DbTable(tableDirectoryPath, tableName, provider);
        } catch (RuntimeException e) {
            newFilePath.toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirWithWrongSubdirectories() throws IOException {
        if (!tableDirectoryPath.toFile().exists()) {
            Files.createDirectory(tableDirectoryPath);
        }
        Path newDir = tableDirectoryPath.resolve("subdirectory");
        Files.createDirectory(newDir);
        new DbTable(tableDirectoryPath, tableName, provider);
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirWithEmptySubdirs() throws IOException {
        if (!tableDirectoryPath.toFile().exists()) {
            Files.createDirectory(tableDirectoryPath);
        }
        Path newDir = tableDirectoryPath.resolve(validDir);
        Files.createDirectory(newDir);
        new DbTable(tableDirectoryPath, tableName, provider);
    }

    @Test(expected = RuntimeException.class)
    public final void testOnCreatedFromDirWithSubdirsWithWrongFiles() throws IOException {
        if (!tableDirectoryPath.toFile().exists()) {
            Files.createDirectory(tableDirectoryPath);
        }
        Path newDir = tableDirectoryPath.resolve(validDir);
        Files.createDirectory(newDir);
        Path newFilePath = newDir.resolve(testFile);
        newFilePath.toFile().createNewFile();
        new DbTable(tableDirectoryPath, tableName, provider);
    }

    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() throws IOException {
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey() throws IOException {
        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1.getIntAt(0), test.get(testKey1).getIntAt(0));
    }

    @Test
    public final void testGetCalledForComittedKey() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1.getIntAt(0), test.get(testKey1).getIntAt(0));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));
        assertNull(test.get(testKey1));
    }


    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsIllegalArgumentExceptionCalledForNullKey() throws IOException {
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsExceptionCalledForNullValue() throws IOException {
        test.put(testKey1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet() throws IOException {
        assertNull(test.put(testKey1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists() throws IOException {
        test.put(testKey1, testValue1);
        assertEquals(testValue1.getIntAt(0), test.put(testKey1, testValue2).getIntAt(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveThrowsExceptionCalledForNullKey() throws IOException {
        test.remove(null);
    }

    @Test
    public final void testRemoveReturnsNullIfKeyIsNotFound() throws IOException {
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));
        test.commit();
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testCommitCreatesRealFileOnTheDisk() throws IOException {
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
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1.getIntAt(0), test.put(testKey1, testValue2).getIntAt(0));
        test.commit();
        assertEquals(testValue2.getIntAt(0), test.get(testKey1).getIntAt(0));
    }

    @Test
    public final void testCommitRemovesExistentKeyAfterCommit() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));
        test.commit();
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testCommitEmptiedAfterLoadingTable() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));
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
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1.getIntAt(0), test.put(testKey1, testValue2).getIntAt(0));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
    }

    @Test
    public final void testRollbackAfterPuttingNewKey() throws IOException {
        assertEquals(0, test.size());
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.size());
        assertEquals(1, test.rollback());
        assertEquals(0, test.size());
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testRollbackNoChanges() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
    }

    @Test
    public final void testListCalledForEmptyTable() throws IOException {
        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable() throws IOException {
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
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2.getIntAt(0), test.remove(testKey2).getIntAt(0));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey1);
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
    }

    @Test
    public void testGetColumnsCount() throws IOException {
        assertEquals(1, test.getColumnsCount());
    }

    @Test
    public void testGetColumnType() throws IOException {
        assertEquals(Integer.class, test.getColumnType(0));
    }

    @Test
    public final void testSizeCalledForNonEmptyNonCommitedTable() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2.getIntAt(0), test.remove(testKey2).getIntAt(0));
        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable() throws IOException {
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2.getIntAt(0), test.remove(testKey2).getIntAt(0));
        assertEquals(1, test.size());
    }

    @After
    public final void tearDown() throws IOException {
        DbTableProvider.recoursiveDelete(testFolder.toFile());
    }

}
