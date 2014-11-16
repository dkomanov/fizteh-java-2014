package ru.fizteh.fivt.students.olga_chupakhina.junit.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.olga_chupakhina.junit.oTable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class oTableTest {
    private final String testDir = System.getProperty("java.io.tmpdir") + File.separator + "DbTestDir";
    private final String tableName = "table1";
    private final String testFile = "Тестовый файл.txt";
    private final int dirNumber = 1;
    private final int fileNumber = 1;
    private String correctKey;
    private final String testKey1 = "ключ1";
    private final String testKey2 = "key2";
    private final String testValue1 = "значение1";
    private final String testValue2 = "value2";
    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;

    @Before
    public void setUp() {
        File dir = new File(testDir);
        dir.mkdir();
        byte[] b = {dirNumber + fileNumber * 16, 'k', 'e', 'y'};
        correctKey = new String(b);
    }

    @Test
    public final void testGetReturnsNullIfKeyIsNotFound() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test  = new oTable(testDir, tableName);
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test  = new oTable(testDir, tableName);
        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(testDir, tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForComittedKey() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.get(testKey1));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsIllegalArgumentExceptionCalledForNullKey() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsExceptionCalledForNullValue() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        test.put(testKey1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        test.put(testKey1, testValue1);
        assertEquals(testValue1, test.put(testKey1, testValue2));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveThrowsExceptionCalledForNullKey() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        test.remove(null);
    }

    @Test
    public final void testRemoveReturnsNullIfKeyIsNotFound() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testCommitCreatesRealFileOnTheDisk()
            throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
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
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.put(testKey1, testValue2));
        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
    }

    //RollbackTests.
    @Test
    public final void testRollbackAfterPuttingNewKey() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertEquals(0, test.size());
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.size());
        assertEquals(1, test.rollback());
        assertEquals(0, test.size());
        assertNull(test.get(testKey1));
    }

    @Test
    public final void testRollbackNoChanges() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
    }

    @Test
    public final void testListCalledForEmptyTable() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        Set<String> expectedKeySet = new HashSet<String>();
        expectedKeySet.add(testKey1);
        expectedKeySet.add(testKey2);
        Set<String> tableKeySet = new HashSet<String>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
    }

    @Test
    public final void testListCalledForNonEmptyCommitedTable() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        Set<String> expectedKeySet = new HashSet<String>();
        expectedKeySet.add(testKey1);
        Set<String> tableKeySet = new HashSet<String>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
    }

    //Size tests.
    @Test
    public final void testSizeCalledForNonEmptyNonCommitedTable() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable() throws Exception {
        String tableDirectoryPath = testDir + File.separator + tableName;
        File tableDir = new File(tableDirectoryPath);
        tableDir.mkdir();
        Table test = new oTable(tableName, testDir);
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
