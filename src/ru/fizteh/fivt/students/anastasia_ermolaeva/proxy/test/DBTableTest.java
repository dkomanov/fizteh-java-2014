package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.Record;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DBTableTest {

    private Path testDirectory;
    private static final String TABLE_NAME = "Таблица1";
    private static final String TEST_KEY1 = "ключ1";
    private static final String TEST_KEY2 = "ключ2";

    private static final int DIR_AMOUNT = 16;
    private static final int FILES_AMOUNT = 16;
    private static final List<Class<?>> TYPES = Arrays.asList(Integer.class);

    private static Storeable testValue1 = new Record(TYPES);
    private static Storeable testValue2 = new Record(TYPES);

    private Table test;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        testDirectory = tempFolder.newFolder().toPath();
        TableProvider provider = new TableHolder(testDirectory.toString());
        testValue1.setColumnAt(0, 1);
        testValue2.setColumnAt(0, 5);
        test = provider.createTable(TABLE_NAME, TYPES);
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
        assertNull(test.get(TEST_KEY1));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetThrowsIllegalArgumentExceptionCalledForNullKey()
            throws IOException {

        test.get(null);
    }

    @Test
    public final void testGetCalledForNonComittedKey()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.get(TEST_KEY1).getIntAt(0));
    }

    @Test
    public final void testGetCalledForComittedKey()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.get(TEST_KEY1).getIntAt(0));
    }

    @Test
    public final void testGetCalledForDeletedKeyBeforeCommit()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.remove(TEST_KEY1).getIntAt(0));

        assertNull(test.get(TEST_KEY1));
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
        test.put(TEST_KEY1, null);
    }

    @Test
    public final void testPutReturnsNullIfKeyHasNotBeenWrittenYet()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));
    }

    @Test
    public final void testPutReturnsOldValueIfKeyExists()
            throws IOException {
        test.put(TEST_KEY1, testValue1);

        assertEquals(testValue1.getIntAt(0), test.put(TEST_KEY1, testValue2).getIntAt(0));
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

        assertNull(test.remove(TEST_KEY1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyBeforeCommit()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.remove(TEST_KEY1).getIntAt(0));

        assertNull(test.remove(TEST_KEY1));
    }

    @Test
    public final void testRemoveCalledForDeletedKeyAfterCommit()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.remove(TEST_KEY1).getIntAt(0));

        test.commit();

        assertNull(test.remove(TEST_KEY1));
    }

    /*
    * CommitTests.
    * Also is checked implicitly inside put, get and remove tests.
    */
    @Test
    public final void testCommitCreatesRealFileOnTheDisk()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        test.commit();
        String subdirectoryName = Math.abs(TEST_KEY1.getBytes(Utility.ENCODING)[0]
                % DIR_AMOUNT) + DBTable.DIR_SUFFIX;
        String fileName = Math.abs((TEST_KEY1.getBytes(Utility.ENCODING)[0]
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

        assertNull(test.put(TEST_KEY1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.put(TEST_KEY1, testValue2).getIntAt(0));

        test.commit();

        assertEquals(testValue2.getIntAt(0), test.get(TEST_KEY1).getIntAt(0));
    }

    @Test
    public final void testCommitRemovesExistentKeyAfterCommit()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.remove(TEST_KEY1).getIntAt(0));

        test.commit();

        assertNull(test.get(TEST_KEY1));
    }

    @Test
    public final void testCommitEmptiedAfterLoadingTable()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        test.commit();

        assertEquals(testValue1.getIntAt(0), test.remove(TEST_KEY1).getIntAt(0));

        test.commit();
        String subdirectoryName = Math.abs(TEST_KEY1.getBytes(Utility.ENCODING)[0]
                % DIR_AMOUNT) + DBTable.DIR_SUFFIX;
        String fileName = Math.abs((TEST_KEY1.getBytes(Utility.ENCODING)[0]
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

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitReturnsNotZeroChangesRewriting()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(testValue1.getIntAt(0), test.put(TEST_KEY1, testValue2).getIntAt(0));

        assertEquals(1, test.commit());
    }

    @Test
    public final void testCommitNoChanges()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

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

        assertNull(test.put(TEST_KEY1, testValue1));

        assertEquals(1, test.size());

        assertEquals(1, test.rollback());

        assertEquals(0, test.size());

        assertNull(test.get(TEST_KEY1));
    }

    @Test
    public final void testRollbackNoChanges()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

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

        assertNull(test.put(TEST_KEY1, testValue1));

        assertNull(test.put(TEST_KEY2, testValue2));

        assertEquals(testValue2.getIntAt(0), test.remove(TEST_KEY2).getIntAt(0));

        assertEquals(1, test.size());
    }

    @Test
    public final void testSizeCalledForNonEmptyCommitedTable()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertNull(test.put(TEST_KEY2, testValue2));

        test.commit();

        assertEquals(testValue2.getIntAt(0), test.remove(TEST_KEY2).getIntAt(0));

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

    /*
    List tests.
     */
    @Test
    public final void testListCalledForEmptyTable()
            throws IOException {

        assertTrue(test.list().isEmpty());
    }

    @Test
    public final void testListCalledForNonEmptyNewTable()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertNull(test.put(TEST_KEY2, testValue2));

        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(TEST_KEY1);
        expectedKeySet.add(TEST_KEY2);
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(test.list());

        assertEquals(expectedKeySet, tableKeySet);
    }

    @Test
    public final void testListCalledForNonEmptyCommitedTable()
            throws IOException {

        assertNull(test.put(TEST_KEY1, testValue1));

        assertNull(test.put(TEST_KEY2, testValue2));

        test.commit();

        assertEquals(testValue2.getIntAt(0), test.remove(TEST_KEY2).getIntAt(0));

        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(TEST_KEY1);
        Set<String> tableKeySet = new HashSet<>();
        tableKeySet.addAll(test.list());

        assertEquals(expectedKeySet, tableKeySet);
    }

    /*
    GetColumn test.
     */
    @Test
    public final void testTableReturnsIntegerCallFirstColumnType() {
        assertEquals(Integer.class, test.getColumnType(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public final void testTableThrowsExceptiobCallColumnTypeWithWrongIndex() {
        test.getColumnType(1);
    }

    /*
    GetColumnsCount test.
     */
    @Test
    public final void testTableReturnsCorrectAmountOfColumns() {
        assertEquals(1, test.getColumnsCount());
    }

    /*
    Close tests.
     */

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallPutAfterClose() {
        ((DBTable) test).close();
        test.put(TEST_KEY1, testValue1);
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallGetAfterClose() {
        ((DBTable) test).close();
        test.get(TEST_KEY1);
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallRemoveAfterClose() {
        ((DBTable) test).close();
        test.remove(TEST_KEY1);
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallSizeAfterClose() {
        ((DBTable) test).close();
        test.size();
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallListAfterClose() {
        ((DBTable) test).close();
        test.list();
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallCommitAfterClose() throws IOException {
        ((DBTable) test).close();
        test.commit();
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallRollbackAfterClose() {
        ((DBTable) test).close();
        test.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallUncommitedChangesAfterClose() {
        ((DBTable) test).close();
        test.getNumberOfUncommittedChanges();
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallGetColumnTypeAfterClose() {
        ((DBTable) test).close();
        test.getColumnType(0);
    }

    @Test(expected = IllegalStateException.class)
    public final void testTableThrowsIllegalStateExceptionCallGetColumnsContAfterClose() {
        ((DBTable) test).close();
        test.getColumnsCount();
    }

}

