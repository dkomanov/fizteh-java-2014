package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.Record;
import ru.fizteh.fivt.students.anastasia_ermolaeva.storeable.TableHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class DBTableTest {
    private final Path testDirectory
            = Paths.get(System.getProperty("fizteh.db.dir"));
    private final String tableName = "Таблица1";
    private final String testKey1 = "ключ1";
    private final String testKey2 = "ключ2";

    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;
    private static List<Class<?>> types;
    private static Storeable testValue1;

    private static Storeable testValue2;

    private TableProvider provider;
    private Table test;

    @Before
    public void setUp() throws Exception {
        if (!Files.exists(testDirectory)) {
            Files.createDirectory(testDirectory);
        }
        provider = new TableHolder(testDirectory.toString());
        types = new ArrayList<>();
        types.add(Integer.class);
        testValue1 = new Record(types);
        testValue1.setColumnAt(0, 1);
        testValue2 = new Record(types);
        testValue2.setColumnAt(0, 5);
        test = provider.createTable(tableName, types);
    }

    @After
    public void tearDown() throws Exception {
        Utility.recursiveDeleteCopy(testDirectory);
    }


    /*
    * GetTests.
     */
    @Test
    public final void testGetReturnsNullIfKeyIsNotFound()
            throws IOException {
        assertNull(test.get(testKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey()
            throws IOException {

        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.get(testKey1).getIntAt(0));
    }

    @Test
    public final void testGetCalledForComittedKey()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.get(testKey1).getIntAt(0));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));

        assertNull(test.get(testKey1));
    }

    /*
    * PutTests.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsIllegalArgumentExceptionCalledForNullKey()
            throws IOException {
        test.put(null, testValue1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutThrowsExceptionCalledForNullValue()
            throws IOException {
        test.put(testKey1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists()
            throws IOException {
        test.put(testKey1, testValue1);

        assertEquals(testValue1.getIntAt(0), test.put(testKey1, testValue2).getIntAt(0));
    }

    /*
    * RemoveTests.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveThrowsExceptionCalledForNullKey()
            throws IOException {

        test.remove(null);
    }

    @Test
    public final void testRemoveReturnsNullIfKeyIsNotFound()
            throws IOException {

        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));

        assertNull(test.remove(testKey1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));

        test.commit();

        assertNull(test.remove(testKey1));
    }

    /*
    * CommitTests.
    * Also is checked implicitly inside put, get and remove tests.
    */
    @Test
    public final void testCommitCreatesRealFileOnTheDisk()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes(Utility.ENCODING)[0]
                % DIR_AMOUNT) + DBTable.DIR_SUFFIX;
        String fileName = Math.abs((testKey1.getBytes(Utility.ENCODING)[0]
                / DIR_AMOUNT) % FILES_AMOUNT)
                + DBTable.FILE_SUFFIX;
        Path filePath = Paths.get(testDirectory.toString(),
                test.getName(),
                subdirectoryName, fileName);

        assertTrue(Files.exists(filePath));
    }

    @Test
    public final void testCommitOverwritesCommitedKey()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.put(testKey1, testValue2).getIntAt(0));

        test.commit();

        assertEquals(testValue2.getIntAt(0), test.get(testKey1).getIntAt(0));
    }

    @Test
    public final void testCommitRemovesExistentKeyAfterCommit()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));

        test.commit();

        assertNull(test.get(testKey1));
    }

    @Test
    public final void testCommitEmptiedAfterLoadingTable()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.remove(testKey1).getIntAt(0));

        test.commit();
        String subdirectoryName = Math.abs(testKey1.getBytes(Utility.ENCODING)[0]
                % DIR_AMOUNT) + DBTable.DIR_SUFFIX;
        String fileName = Math.abs((testKey1.getBytes(Utility.ENCODING)[0]
                / DIR_AMOUNT) % FILES_AMOUNT)
                + DBTable.FILE_SUFFIX;
        Path filePath = Paths.get(testDirectory.toString(),
                test.getName(),
                subdirectoryName, fileName);

        assertFalse(Files.exists(filePath));
    }

    @Test
    public final void testCommitReturnsNonZeroChangesPuttingNewRecord()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.put(testKey1, testValue2).getIntAt(0));

        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertEquals(1, test.commit());

        assertEquals(0, test.commit());
    }

    /*
    * RollbackTests.
     */
    @Test
    public final void testRollbackAfterPuttingNewKey()
            throws IOException {

        assertEquals(0, test.size());

        assertNull(test.put(testKey1, testValue1));

        assertEquals(1, test.size());

        assertEquals(1, test.rollback());

        assertEquals(0, test.size());

        assertNull(test.get(testKey1));
    }

    @Test
    public final void testRollbackNoChanges()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        test.rollback();

        assertEquals(0, test.size());

        assertEquals(0, test.rollback());
    }

    /*
    * Size tests.
     */
    @Test
    public final void testSizeCalledForNonEmptyNonCommitedTable()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertNull(test.put(testKey2, testValue2));

        assertEquals(testValue2.getIntAt(0), test.remove(testKey2).getIntAt(0));

        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable()
            throws IOException {

        assertNull(test.put(testKey1, testValue1));

        assertNull(test.put(testKey2, testValue2));

        test.commit();

        assertEquals(testValue2.getIntAt(0), test.remove(testKey2).getIntAt(0));

        assertEquals(1, test.size());
    }

    @Test
    public void testGetColumnsCount() throws IOException {

        assertEquals(1, test.getColumnsCount());
    }

    @Test
    public void testGetColumnType() throws IOException {

        assertEquals(Integer.class, test.getColumnType(0));
    }

    //List tests.
    @Test
    public final void testListCalledForEmptyTable()
            throws IOException {

        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable()
            throws IOException {

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
    public final void testListCalledForNonEmptyCommitedTable()
            throws IOException {

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
}