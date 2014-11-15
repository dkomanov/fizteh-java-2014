package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DbTable;

public class DbTableTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    private final String tableName = "table1";
    //The table directory must contain only files in such format:
    //"[path to the table directory]/*.dir/*.dat".
    private final int dirNumber = 1;
    private final int fileNumber = 1;
    private final String wrongSubfileName = "wrong";
    private final String requiredSubdirectoryName = dirNumber + ".dir";
    private final String requiredSubfileName = fileNumber + ".dat";
    private String correctKey;
    private final String testKey = "key";
    private final String anotherKey = "key2";
    private final String testValue = "val";
    private final int offsetLength = 4;
    
    @Before
    public void setUp() {
        testDir.toFile().mkdir();
        //Each key should be placed to directory with number 
        //key.getBytes()[0] % NUMBER_OF_PARTITIONS
        //and file with number
        //(key.getBytes()[0] / NUMBER_OF_PARTITIONS) % NUMBER_OF_PARTITIONS.
        byte[] b = {dirNumber + fileNumber * DbTable.NUMBER_OF_PARTITIONS, 'k', 'e', 'y'};
        correctKey = new String(b);
    }

    @Test
    public void testDbTableCreatedForNonexistentDirectory() {
        new DbTable(testDir, tableName);
    }
    
    @Test(expected = RuntimeException.class)
    public void testDbTableThrowsExceptionLoadedDirectoryWithWrongNamedSubdirectory() {
        testDir.resolve(wrongSubfileName).toFile().mkdir();
        new DbTable(testDir, tableName);
    }
    
    @Test(expected = RuntimeException.class)
    public void testDbTableThrowsExceptionLoadedDirectoryWithEmptySubdirectory() {
        testDir.resolve(requiredSubdirectoryName).toFile().mkdir();
        new DbTable(testDir, tableName);
    }
    
    @Test(expected = RuntimeException.class)
    public void testDbTableThrowsExceptionLoadedDirectoryWithSubfileNotInSubdirectory()
            throws IOException {
        testDir.resolve(requiredSubdirectoryName).toFile().createNewFile();
        new DbTable(testDir, tableName);
    }
    
    @Test(expected = RuntimeException.class)
    public void testDbTableThrowsExceptionLoadedDirectoryWithWrongNamedFileInSubdirectory()
            throws IOException {
        Path subdirectoryPath = testDir.resolve(requiredSubdirectoryName);
        subdirectoryPath.toFile().mkdir();
        subdirectoryPath.resolve(wrongSubfileName).toFile().createNewFile();
        new DbTable(testDir, tableName);
    }
    
    @Test(expected = RuntimeException.class)
    public void testDbTableThrowsExceptionLoadedDirectoryWithDirectoryInSubdirectory()
            throws IOException {
        Path subdirectoryPath = testDir.resolve(requiredSubdirectoryName);
        subdirectoryPath.toFile().mkdir();
        subdirectoryPath.resolve(requiredSubfileName).toFile().mkdir();
        new DbTable(testDir, tableName);
    }
    
    @Test
    public void testDbTableLoadedCorrectNonemptyDirectory() throws IOException {
        Path subdirectoryPath = testDir.resolve(requiredSubdirectoryName);
        subdirectoryPath.toFile().mkdir();
        Path subfilePath = subdirectoryPath.resolve(requiredSubfileName);
        try (DataOutputStream file
                = new DataOutputStream(new FileOutputStream(subfilePath.toString()))) {
            file.write(correctKey.getBytes(DbTable.CODING));
            file.write('\0');
            file.writeInt(correctKey.length() + 1 + offsetLength);
            file.write(testValue.getBytes(DbTable.CODING));
        }
        new DbTable(testDir, tableName);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionCalledForNullKeyAndNonNullValue() {
        Table test = new DbTable(testDir, tableName);
        test.put(null, testValue);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionCalledForNonNullKeyAndNullValue() {
        Table test = new DbTable(testDir, tableName);
        test.put(testKey, null);
    }
    
    @Test
    public void testCommitPuttingNonNullKeyAndValue() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
    }
    
    @Test
    public void testCommitPuttingTwiceNonNullKeyAndValue() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(testValue, test.put(testKey, testValue));
        test.commit();
    }
    
    @Test
    public void testCommitOverwritingCommitedKey() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.put(testKey, testValue));
        test.commit();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsExceptionCalledForNullKey() {
        Table test = new DbTable(testDir, tableName);
        test.get(null);
    }
    
    @Test
    public void testGetCalledForNonexistentKey() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.get(testKey));
    }
    
    @Test
    public void testGetCalledForNonComittedExistentKey() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(testValue, test.get(testKey));
    }
    
    @Test
    public void testGetCalledForComittedExistentKey() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.get(testKey));
    }
    
    @Test
    public void testRollbackAfterPuttingNewKey() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(0, test.size());
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(1, test.size());
        test.rollback();
        assertEquals(0, test.size());
        assertEquals(null, test.get(testKey));
    }
    
    @Test
    public void testRollbackWithoutAnyChanges() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.rollback();
        assertEquals(0, test.size());
        test.rollback();
        assertEquals(0, test.size());
    }
    
    @Test(expected = RuntimeException.class)
    public void testRemoveThrowsExceptionCalledForNullKey() {
        Table test = new DbTable(testDir, tableName);
        test.remove(null);
        test.commit();
    }
    
    @Test
    public void testCommitRemovingNonexistentKeyFromNonCommitedFile() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.remove(testKey));
        test.commit();
    }
    
    @Test
    public void testCommitRemovingExistentKeyFromNonCommitedFile() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(testValue, test.remove(testKey));
        test.commit();
    }
    
    @Test
    public void testCommitRemovingExistentKeyFromCommitedFile() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.remove(testKey));
        test.commit();
    }
    
    @Test
    public void testCommitRemovingNonexistentKeyFromCommitedFile() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.remove(testKey));
        test.commit();
    }
    
    @Test
    public void testListCalledForEmptyTable() {
        Table test = new DbTable(testDir, tableName);
        assertTrue(test.list().isEmpty());
    }
    
    @Test
    public void testListCalledForNonEmptyNewTable() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(null, test.put(anotherKey, testValue));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey);
        expectedKeySet.add(anotherKey);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(test.list());
        assertEquals(expectedKeySet, actualKeySet);
    }
    
    @Test
    public void testListCalledForNonEmptyCommitedTable() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(null, test.put(anotherKey, testValue));
        test.commit();
        assertEquals(testValue, test.remove(anotherKey));
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(testKey);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(test.list());
        assertEquals(expectedKeySet, actualKeySet);
    }
    
    @Test
    public void testSizeCalledForEmptyTable() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(0, test.size());
    }
    
    @Test
    public void testSizeCalledForNonemptyNonCommitedTable() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(null, test.put(anotherKey, testValue));
        assertEquals(testValue, test.remove(anotherKey));
        assertEquals(1, test.size());
    }
    
    @Test
    public void testSizeCalledForNonemptyCommitedTable() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(null, test.put(anotherKey, testValue));
        test.commit();
        assertEquals(testValue, test.remove(anotherKey));
        assertEquals(1, test.size());
    }
    
    @Test
    public void testCommitEmptiedAfterLoadingTable() {
        Table test = new DbTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.remove(testKey));
        test.commit();
        String subdirectoryName = testKey.getBytes()[0] % 16 + ".dir";
        String fileName = (testKey.getBytes()[0] / 16) % 16 + ".dat";
        Path filePath = Paths.get(testDir.toString(), subdirectoryName, fileName);
        assertFalse(filePath.toFile().exists());
    }
    
    @After
    public void tearDown() {
        for (File curFile : testDir.toFile().listFiles()) {
            if (curFile.isDirectory()) {
                for (File subFile : curFile.listFiles()) {
                    subFile.delete();
                }
            }
            curFile.delete();
        }
        testDir.toFile().delete();
    }
}
