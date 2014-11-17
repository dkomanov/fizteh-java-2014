package ru.fizteh.fivt.students.alina_chupakhina.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.alina_chupakhina.junit.BdTable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BdTableTest {
    private final String testDir = System.getProperty("java.io.tmpdir") + File.separator + "DbTestDir";
    private final String tableName = "table1";
    private final String testFile = "Тестовый файл.txt";
    private final int dirNumber = 1;
    private final int fileNumber = 1;
    private String correctKey;
    private final String testKey1 = "ключ1";
    private final String testKey2 = "ключ2";
    private final String testValue1 = "значение1";
    private final String testValue2 = "значение2";
    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;
    private Table test;

    @Before
    public void setUp() {
        File dir = new File(testDir);
        dir.mkdir();
        byte[] b = {dirNumber + fileNumber * 16, 'k', 'e', 'y'};
        correctKey = new String(b);
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        test = new BdTable(tableName, testDir);
    }

    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() throws Exception {
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey() throws Exception {
        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForComittedKey() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsIllegalArgumentExceptionCalledForNullKey() throws Exception {
        Table test = new BdTable(tableName, testDir);
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsExceptionCalledForNullValue() throws Exception {
        test.put(testKey1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet() throws Exception {
        assertNull(test.put(testKey1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists() throws Exception {
        test.put(testKey1, testValue1);
        assertEquals(testValue1, test.put(testKey1, testValue2));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveThrowsExceptionCalledForNullKey() throws Exception {
        test.remove(null);
    }

    @Test
    public final void testRemoveReturnsNullIfKeyIsNotFound() throws Exception {
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testCommitCreatesRealFileOnTheDisk()
            throws Exception {
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes("UTF-8")[0]
                % DIR_AMOUNT) + ".dir";
        String fileName = Math.abs((testKey1.getBytes("UTF-8")[0]
                / DIR_AMOUNT) % FILES_AMOUNT)
                + ".dat";
        Path filePath = Paths.get(testDir.toString(),
                test.getName(),
                subdirectoryName, fileName);
        assertTrue(filePath.toFile().exists());
    }

    @Test
    public final void testCommitReturnsNonZeroChangesPuttingNewRecord() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.put(testKey1, testValue2));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
    }

    //RollbackTests.
    @Test
    public final void testRollbackAfterPuttingNewKey() throws Exception {
        assertEquals(0, test.size());
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.size());
        assertEquals(1, test.rollback());
        assertEquals(0, test.size());
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testRollbackNoChanges() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
    }

    @Test
    public final void testListCalledForEmptyTable() throws Exception {
        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable() throws Exception {
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
    public final void testListCalledForNonEmptyCommitedTable() throws Exception {
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
    public final void testGetReturnsLatestValueForUncommittedKey() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable() throws Exception {
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @After
    public void tearDown() {
        File dir = new File(testDir);
        for (File currentTableDirectory : dir.listFiles()) {
            if (currentTableDirectory.isDirectory()) {
                for (File tableSubDirectory
                        :currentTableDirectory.listFiles()) {
                    if (tableSubDirectory.isDirectory()) {
                        for (File tableFile: tableSubDirectory.listFiles()) {
                            tableFile.delete();
                        }
                    }
                    tableSubDirectory.delete();
                }
            }
            currentTableDirectory.delete();
        }
        dir.delete();
    }
}
