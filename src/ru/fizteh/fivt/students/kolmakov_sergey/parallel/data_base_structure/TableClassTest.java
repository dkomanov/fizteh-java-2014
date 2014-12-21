package ru.fizteh.fivt.students.kolmakov_sergey.parallel.data_base_structure;

import static org.junit.Assert.*;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.parallel.data_base_exceptions.DatabaseCorruptedException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TableClassTest {
    private final int offsetLength = 4;
    private final int folderIndex = 1;
    private final int fileIndex = 1;
    private final String encoding = "UTF-8";
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final String tableName = "table";
    private final String unexpectedFolderName = "unexpected";
    private final String folderName = folderIndex + ".dir";
    private final String dataFileName = fileIndex + ".dat";
    private String correctKeyStoredInDataFile;
    private final String testKey1 = "key1";
    private final String testKey2 = "key2";
    private List<Class<?>> columnTypes = new ArrayList<>();
    private Table table;
    private TableProvider provider = new TableManager(testDir.toString());
    List<String> values = new ArrayList<>();
    private Storeable testStoreableValue;
    

    @Before
    public void setUp() throws DatabaseCorruptedException {
        values.add("\"string value\"");
        columnTypes.add(String.class);
        table = new TableClass(testDir, tableName, provider, columnTypes);
        testStoreableValue = new StoreableClass(table, values);
        testDir.toFile().mkdir();
        byte[] b = {folderIndex + fileIndex * TableManager.NUMBER_OF_PARTITIONS, 'k', 'e', 'y'};
        correctKeyStoredInDataFile = new String(b);
    }

    @Test
    public void testTableClassConstructorWhenDatabaseIsEmpty() throws DatabaseCorruptedException {
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testTableClassThrowsExceptionWhenUnexpectedFolderFound() throws DatabaseCorruptedException {
        testDir.resolve(unexpectedFolderName).toFile().mkdir();
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testTableClassThrowsExceptionWhenEmptyFolderFound() throws DatabaseCorruptedException {
        testDir.resolve(folderName).toFile().mkdir();
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testTableClassThrowsExceptionWhenFileFoundInWrongPlace()
            throws IOException, DatabaseCorruptedException {
        testDir.resolve(folderName).toFile().createNewFile();
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testTableClassThrowsExceptionWhenDirectoryFoundInWrongPlace()
            throws IOException, DatabaseCorruptedException {
        Path folderPath = testDir.resolve(folderName);
        folderPath.toFile().mkdir();
        folderPath.resolve(dataFileName).toFile().mkdir();
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testTableClassThrowsExceptionWhenFolderHasBadName()
            throws IOException, DatabaseCorruptedException {
        Path folderPath = testDir.resolve(folderName);
        folderPath.toFile().mkdir();
        folderPath.resolve(unexpectedFolderName).toFile().createNewFile();
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test
    public void testTableClassConstructorWhenDatabaseIsNotEmpty() throws IOException, DatabaseCorruptedException {
        Path folderPath = testDir.resolve(folderName);
        folderPath.toFile().mkdir();
        Path subfilePath = folderPath.resolve(dataFileName);
        try (DataOutputStream file
                     = new DataOutputStream(new FileOutputStream(subfilePath.toString()))) {
            file.write(correctKeyStoredInDataFile.getBytes(encoding));
            file.write('\0');
            file.writeInt(correctKeyStoredInDataFile.length() + 1 + offsetLength);
            file.write(provider.serialize(table, testStoreableValue).getBytes(encoding));
        }
        new TableClass(testDir, tableName, provider, columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionWhenKeyIsNull() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        test.put(null, testStoreableValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionWhenValueIsNull() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        test.put(testKey1, null);
    }

    @Test
    public void testCommitAfterPuttingData() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        test.commit();
    }

    @Test
    public void testCommitPuttingDataWhenKeyExistsAlready() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(testStoreableValue, test.put(testKey1, testStoreableValue));
        test.commit();
    }

    @Test
    public void testDuplicationCommitOfTheSameKey() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        test.commit();
        assertEquals(testStoreableValue, test.put(testKey1, testStoreableValue));
        test.commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsExceptionWhenKeyIsNull() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        test.get(null);
    }

    @Test
    public void testGetReturnsNullWhenKeyNotFound() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.get(testKey1));
    }

    @Test
    public void testGetWhenUncommittedKeyExists() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(testStoreableValue, test.get(testKey1));
    }

    @Test
    public void testGetWhenCommittedKeyExists() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        test.commit();
        assertEquals(testStoreableValue, test.get(testKey1));
    }

    @Test
    public void testRollbackAfterPuttingNewKey() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(0, test.size());
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(1, test.size());
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(null, test.get(testKey1));
    }

    @Test
    public void testRollbackWithoutChanges() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        test.rollback();
        assertEquals(0, test.size());
        test.rollback();
        assertEquals(0, test.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveThrowsExceptionWhenKeyIsNull() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        test.remove(null);
        test.commit();
    }

    @Test
    public void testCommitRemovingUncommittedKey() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(testStoreableValue, test.remove(testKey1));
        test.commit();
    }

    @Test
    public void testRemoveKeyWhenDatafileDoesNotExist() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.remove(testKey1));
        test.commit();
    }

    @Test
    public void testCommitRemovingCommittedKey() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        test.commit();
        assertEquals(testStoreableValue, test.remove(testKey1));
        test.commit();
    }

    @Test
    public void testListCalledForEmptyTable() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public void testListCalledForNonEmptyUncommitedTable() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(null, test.put(testKey2, testStoreableValue));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey1);
        expectedKeySet.add(testKey2);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(test.list());
        assertEquals(expectedKeySet, actualKeySet);
    }

    @Test
    public void testListCalledForNonEmptyCommitedTable() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(null, test.put(testKey2, testStoreableValue));
        test.commit();
        assertEquals(testStoreableValue, test.remove(testKey2));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey1);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(test.list());
        assertEquals(expectedKeySet, actualKeySet);
    }

    @Test
    public void testSizeCalledForEmptyTable() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(0, test.size());
    }

    @Test
    public void testSizeCalledForUncommitedTable() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(null, test.put(testKey2, testStoreableValue));
        assertEquals(testStoreableValue, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public void testSizeCalledForNonemptyCommitedTable() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        assertEquals(null, test.put(testKey2, testStoreableValue));
        test.commit();
        assertEquals(testStoreableValue, test.remove(testKey2));
        assertEquals(1, test.size());
    }

    @Test
    public void testCommitDeletesEmptyFolders() throws DatabaseCorruptedException, IOException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(null, test.put(testKey1, testStoreableValue));
        test.commit();
        assertEquals(testStoreableValue, test.remove(testKey1));
        test.commit();
        String folderName = testKey1.getBytes()[0] % TableManager.NUMBER_OF_PARTITIONS + ".dir";
        String fileName = (testKey1.getBytes()[0] / TableManager.NUMBER_OF_PARTITIONS)
                % TableManager.NUMBER_OF_PARTITIONS + ".dat";
        Path filePath = Paths.get(testDir.toString(), folderName, fileName);
        assertFalse(filePath.toFile().exists());
    }

    @Test
    public void testGetNameMethod() throws DatabaseCorruptedException {
        Table test = new TableClass(testDir, tableName, provider, columnTypes);
        assertEquals(tableName, test.getName());
    }
    @Test
    public void testGetNumberOfChangesMethod() throws DatabaseCorruptedException {
        TableClass test = new TableClass(testDir, tableName, provider, columnTypes);
        test.put(testKey1, testStoreableValue);
        assertEquals(1, test.getNumberOfUncommittedChanges());
        test.put(testKey2, testStoreableValue);
        assertEquals(2, test.getNumberOfUncommittedChanges());
    }


    @After
    public void tearDown() {
        deleteRecursively(testDir.toFile());
    }
    private static void deleteRecursively(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    deleteRecursively(currentFile);
                }
            } catch (NullPointerException e) {
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}
