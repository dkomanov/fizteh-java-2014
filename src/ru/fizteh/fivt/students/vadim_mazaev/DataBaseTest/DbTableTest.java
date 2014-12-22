package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DataBaseIOException;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DbTable;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Helper;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManager;

@SuppressWarnings("resource")
public class DbTableTest {
    private TableManager test;
    private Table testTable;
    private Storeable testStoreable;
    private Storeable wrongStoreable;
    // The table directory must contain only files in such format:
    // "[path to the table directory]/*.dir/*.dat".
    private final String testKey = "key";
    private final String anotherKey = "key2";

    @Before
    public void setUp() throws IOException, ParseException {
        TestHelper.TEST_DIR.toFile().mkdir();
        test = new TableManager(TestHelper.TEST_DIR.toString());
        test.createTable(TestHelper.TEST_TABLE_NAME, TestHelper.STRUCTURE);
        testTable = test.getTable(TestHelper.TEST_TABLE_NAME);
        testStoreable = test.deserialize(testTable, TestHelper.SERIALIZED_VALUES);
        test.createTable("tempTable", TestHelper.MIXED_STRUCTURE);
        Table tempTable = test.getTable("tempTable");
        wrongStoreable = test.deserialize(tempTable, TestHelper.MIXED_SERIALIZED_VALUES);
        test.removeTable("tempTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDbTableThrowsExceptionCreatedForNullProvider() throws IOException {
        new DbTable(null, TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDbTableThrowsExceptionCreatedForNullPathToTableDirectory() throws IOException {
        new DbTable(test, null);
    }

    @Test
    public void testGettingNameOfTable() {
        assertEquals(TestHelper.TEST_TABLE_NAME, testTable.getName());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnTypeThrowsExceptionForColumnIndexOutOfBounds() {
        testTable.getColumnType(TestHelper.STRUCTURE.size() + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionCalledForNullKeyAndNonNullValue() {
        testTable.put(null, testStoreable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionCalledForNonNullKeyAndNullValue() {
        testTable.put(testKey, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testPutThrowsExceptionForValueWithWrongColumnStructure() {
        testTable.put(testKey, wrongStoreable);
    }

    @Test
    public void testCommitPuttingNonNullKeyAndValue() throws IOException {
        assertEquals(null, testTable.put(testKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test
    public void testCommitPuttingTwiceNonNullKeyAndValue() throws IOException {
        assertEquals(null, testTable.put(testKey, testStoreable));
        assertEquals(testStoreable, testTable.put(testKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test
    public void testCommitOverwritingCommitedKey() throws IOException {
        assertEquals(null, testTable.put(testKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        assertEquals(testStoreable, testTable.put(testKey, testStoreable));
        numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsExceptionCalledForNullKey() {
        testTable.get(null);
    }

    @Test
    public void testGetCalledForNonexistentKey() {
        assertNull(testTable.get(testKey));
    }

    @Test
    public void testGetCalledForNonComittedExistentKey() {
        assertNull(testTable.put(testKey, testStoreable));
        assertEquals(testStoreable, testTable.get(testKey));
    }

    @Test
    public void testGetCalledForComittedExistentKey() throws IOException {
        assertNull(testTable.put(testKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        assertEquals(testStoreable, testTable.get(testKey));
    }

    @Test
    public void testRollbackAfterPuttingNewKey() {
        assertEquals(0, testTable.size());
        assertNull(testTable.put(testKey, testStoreable));
        assertEquals(1, testTable.size());
        testTable.rollback();
        assertEquals(0, testTable.size());
        assertNull(testTable.get(testKey));
    }

    @Test
    public void testRollbackWithoutAnyChanges() {
        assertEquals(null, testTable.put(testKey, testStoreable));
        testTable.rollback();
        assertEquals(0, testTable.size());
        testTable.rollback();
        assertEquals(0, testTable.size());
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveThrowsExceptionCalledForNullKey() throws IOException {
        testTable.remove(null);
        testTable.commit();
    }

    @Test
    public void testCommitRemovingNonexistentKeyFromNonCommitedFile() throws IOException {
        assertNull(testTable.remove(testKey));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test
    public void testCommitRemovingExistentKeyFromNonCommitedFile() throws IOException {
        assertNull(testTable.put(testKey, testStoreable));
        assertEquals(testStoreable, testTable.remove(testKey));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test
    public void testCommitRemovingExistentKeyFromCommitedFile() throws IOException {
        assertNull(testTable.put(testKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        assertEquals(testStoreable, testTable.remove(testKey));
        numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test
    public void testCommitRemovingNonexistentKeyFromCommitedFile() throws IOException {
        assertNull(testTable.remove(testKey));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
    }

    @Test
    public void testListCalledForEmptyTable() {
        assertTrue(testTable.list().isEmpty());
    }

    @Test
    public void testListCalledForNonEmptyNewTable() {
        assertNull(testTable.put(testKey, testStoreable));
        assertNull(testTable.put(anotherKey, testStoreable));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey);
        expectedKeySet.add(anotherKey);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(testTable.list());
        assertEquals(expectedKeySet, actualKeySet);
    }

    @Test
    public void testListCalledForNonEmptyCommitedTable() throws IOException {
        assertNull(testTable.put(testKey, testStoreable));
        assertNull(testTable.put(anotherKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        assertEquals(testStoreable, testTable.remove(anotherKey));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(testTable.list());
        assertEquals(expectedKeySet, actualKeySet);
    }

    @Test
    public void testSizeCalledForEmptyTable() {
        assertEquals(0, testTable.size());
    }

    @Test
    public void testSizeCalledForNonemptyNonCommitedTable() {
        assertNull(testTable.put(testKey, testStoreable));
        assertNull(testTable.put(anotherKey, testStoreable));
        assertEquals(testStoreable, testTable.remove(anotherKey));
        assertEquals(1, testTable.size());
    }

    @Test
    public void testSizeCalledForNonemptyCommitedTable() throws IOException {
        assertNull(testTable.put(testKey, testStoreable));
        assertNull(testTable.put(anotherKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        assertEquals(testStoreable, testTable.remove(anotherKey));
        assertEquals(1, testTable.size());
    }

    @Test
    public void testCommitEmptiedAfterLoadingTable() throws IOException {
        assertNull(testTable.put(testKey, testStoreable));
        int numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        assertEquals(testStoreable, testTable.remove(testKey));
        numberOfChanges = testTable.getNumberOfUncommittedChanges();
        assertEquals(numberOfChanges, testTable.commit());
        String subdirectoryName = testKey.getBytes()[0] % 16 + ".dir";
        String fileName = (testKey.getBytes()[0] / 16) % 16 + ".dat";
        Path filePath = Paths.get(TestHelper.TEST_DIR.toString(), subdirectoryName, fileName);
        assertFalse(filePath.toFile().exists());
    }

    @Test(expected = IllegalStateException.class)
    public void testCommitCallInRemovedTable() throws IOException {
        test.removeTable(TestHelper.TEST_TABLE_NAME);
        testTable.commit();
    }

    // Hereinafter we will create new TableManager to reload the table.
    // Old manager will help us to prepare data and will not be used after that.
    @Test
    public void testReloadTableUsingNewTableManager() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.put(anotherKey, testStoreable);
        testTable.commit();

        TableManager test2 = new TableManager(TestHelper.TEST_DIR.toString());
        assertNotNull(test2.getTable(TestHelper.TEST_TABLE_NAME));
        test2.close();
    }

    @Test(expected = DataBaseIOException.class)
    public void testReloadCorruptedTableWithoutSignatureFile() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.commit();

        Paths.get(TestHelper.TEST_DIR.toString(), TestHelper.TEST_TABLE_NAME, Helper.SIGNATURE_FILE_NAME).toFile()
                .delete();
        new TableManager(TestHelper.TEST_DIR.toString());
    }

    @Test(expected = DataBaseIOException.class)
    public void testReloadTableWithCorruptedEmptySignatureFile() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.commit();

        File signatureFile = Paths.get(TestHelper.TEST_DIR.toString(), TestHelper.TEST_TABLE_NAME,
                Helper.SIGNATURE_FILE_NAME).toFile();
        signatureFile.delete();
        signatureFile.createNewFile();

        new TableManager(TestHelper.TEST_DIR.toString());
    }

    @Test(expected = DataBaseIOException.class)
    public void testReloadTableWithCorruptedSignatureFile() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.commit();

        File signatureFile = Paths.get(TestHelper.TEST_DIR.toString(), TestHelper.TEST_TABLE_NAME,
                Helper.SIGNATURE_FILE_NAME).toFile();
        try (PrintWriter printer = new PrintWriter(signatureFile.toString())) {
            printer.print("unsupportedType");
        }

        new TableManager(TestHelper.TEST_DIR.toString());
    }

    @Test(expected = DataBaseIOException.class)
    public void testReloadTableWithCorruptedDirectory() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.commit();

        File tableDirectory = TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME).toFile();
        for (File subdirectory : tableDirectory.listFiles()) {
            if (subdirectory.isDirectory()) {
                for (File file : subdirectory.listFiles()) {
                    file.delete();
                }
            }
        }

        new TableManager(TestHelper.TEST_DIR.toString());
    }

    @Test(expected = DataBaseIOException.class)
    public void testReloadTableWithCorruptedDirectory2() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.commit();

        File tableDirectory = TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME).toFile();
        for (File subdirectory : tableDirectory.listFiles()) {
            if (subdirectory.isDirectory()) {
                Path newName = tableDirectory.toPath().resolve("wrongName");
                subdirectory.renameTo(newName.toFile());
                break;
            }
        }

        new TableManager(TestHelper.TEST_DIR.toString());
    }

    @Test(expected = DataBaseIOException.class)
    public void testReloadTableWithCorruptedDirectory3() throws IOException {
        testTable.put(testKey, testStoreable);
        testTable.commit();

        File tableDirectory = TestHelper.TEST_DIR.resolve(TestHelper.TEST_TABLE_NAME).toFile();
        for (File subdirectory : tableDirectory.listFiles()) {
            if (subdirectory.isDirectory()) {
                for (File file : subdirectory.listFiles()) {
                    Path newName = Paths.get(tableDirectory.getAbsolutePath(), subdirectory.getName(), "wrongName");
                    file.renameTo(newName.toFile());
                }
                break;
            }
        }

        new TableManager(TestHelper.TEST_DIR.toString());
    }

    @After
    public void tearDown() throws IOException {
        Helper.recoursiveDelete(TestHelper.TEST_DIR);
    }
}
