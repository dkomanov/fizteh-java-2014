package ru.fizteh.fivt.students.egor_belikov.JUnit.UnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.egor_belikov.JUnit.MyTable;
import ru.fizteh.fivt.students.egor_belikov.JUnit.MyTableProvider;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class UnitTestsForTable {
    private final String testDir = System.getProperty("user.dir") + File.separator + "db";//System.getProperty("java.io.tmpdir") + File.separator + "DbTestDir";
    private final String tableName = "table1";
    private final String testFile = "Тестовый файл.txt";
    private final int dirNumber = 1;
    private final int fileNumber = 1;
    private String correctKey;
    private final String testKey1 = "ключ №1";
    private final String testKey2 = "key #2";
    private final String testValue1 = "значение №1";
    private final String testValue2 = "value #2";
    private static MyTableProvider myTableProvider = null;

    @Before
    public void setUp() {
        File dir = new File(testDir);
        dir.mkdir();
        byte[] b = {dirNumber + fileNumber * 16, 'k', 'e', 'y'};
        correctKey = new String(b);
        myTableProvider = new MyTableProvider(testDir);
    }

    @Test
    public final void getNullNoKey() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.get(testKey1));
        myTableProvider.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getExceptionNull() throws Exception {
        Table test  = new MyTable(tableName);
        test.get(null);
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void getNoKey() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.get(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void getGood() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        assertEquals(testValue1, test.get(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void getKeyBeforeCommit() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.get(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test(expected = IllegalArgumentException.class)
    public final void putKeyNull() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        test.put(null, testValue1);
        myTableProvider.removeTable(tableName);

    }

    @Test(expected = IllegalArgumentException.class)
    public final void putValueNull() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        test.put(testKey1, null);
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void putNullNoKey() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void putOldValue() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        test.put(testKey1, testValue1);
        assertEquals(testValue1, test.put(testKey1, testValue2));
        myTableProvider.removeTable(tableName);

    }

    @Test(expected = IllegalArgumentException.class)
    public final void removeKeyNull() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        test.remove(null);
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void removeKeyNotFound() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.remove(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void removeKeyDeletedBeforeCommit() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        assertNull(test.remove(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void removeKeyDeletedAfterCommit() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.remove(testKey1));
        test.commit();
        assertNull(test.remove(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void commitCheckFiles()
            throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        test.commit();
        String subdirectoryName = Math.abs(testKey1.hashCode()
                % 16) + ".dir";
        String fileName = Math.abs((testKey1.hashCode()
                / 16) % 16)
                + ".dat";
        Path filePath = Paths.get(testDir.toString(),
                test.getName(),
                subdirectoryName, fileName);
        assertTrue(filePath.toFile().exists());
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void commitMoreThanZeroChanges() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void commitMoreThanZeroChangesRewrite() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(testValue1, test.put(testKey1, testValue2));
        assertEquals(1, test.commit());
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void commitNoChanges() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.commit());
        assertEquals(0, test.commit());
        myTableProvider.removeTable(tableName);

    }

    //RollbackTests.
    @Test
    public final void rollbackAfterNewKey() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertEquals(0, test.size());
        assertNull(test.put(testKey1, testValue1));
        assertEquals(1, test.size());
        assertEquals(1, test.rollback());
        assertEquals(0, test.size());
        assertNull(test.get(testKey1));
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void rollbackNoChanges() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(0, test.rollback());
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void listEmptyMyTable() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertTrue(test.list().isEmpty());
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void listNotEmptyNewTable() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        Set<String> expectedKeySet = new HashSet<String>();
        expectedKeySet.add(testKey1);
        expectedKeySet.add(testKey2);
        Set<String> tableKeySet = new HashSet<String>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void listNotEmptyCommittedTable() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        Set<String> expectedKeySet = new HashSet<String>();
        expectedKeySet.add(testKey1);
        Set<String> tableKeySet = new HashSet<String>();
        tableKeySet.addAll(test.list());
        assertEquals(expectedKeySet, tableKeySet);
        myTableProvider.removeTable(tableName);

    }

    //Size tests.
    @Test
    public final void sizeNonEmptyNonCommittedTable() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
        myTableProvider.removeTable(tableName);

    }

    @Test
    public final void sizeNonEmptyCommittedTable() throws Exception {
        myTableProvider.createTable(tableName);
        Table test = myTableProvider.getTable(tableName);
        assertNull(test.put(testKey1, testValue1));
        assertNull(test.put(testKey2, testValue2));
        test.commit();
        assertEquals(testValue2, test.remove(testKey2));
        assertEquals(1, test.size());
        myTableProvider.removeTable(tableName);
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
