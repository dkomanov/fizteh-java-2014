package ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_structure;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.students.kolmakov_sergey.storeable.util.Coordinates;

public class DataFileTest {
    private final int offsetLength = 4;
    private final int folderIndex = 1;
    private final int fileIndex = 1;
    private final String encoding = "UTF-8";
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DataBaseTestDirectory");
    private final Path filePath = Paths.get(testDir.toString(), folderIndex + ".dir", fileIndex + ".dat");
    private String wrongKey;
    private String correctKey1;
    private String correctKey2;
    private List<Class<?>> columnTypes = new ArrayList<>();
    private Table table;
    private TableProvider provider = new TableManager(testDir.toString());
    private List<String> values = new ArrayList<>();
    private String testStringValue;
    private Storeable testStoreableValue;

    @Before
    public void setUp() throws DatabaseCorruptedException {
        values.add("\"string value\"");
        columnTypes.add(String.class);
        table = new TableClass(testDir, "table name", provider, columnTypes);
        testStoreableValue = new StoreableClass(table, values);
        testDir.toFile().mkdir();
        correctKey1 = new String(new byte[] {folderIndex + fileIndex * 16, 'k', 'e', 'y', '1'});
        correctKey2 = new String(new byte[] {folderIndex + fileIndex * 16, 'k', 'e', 'y', '2'});
        //Wrong key will contain changed first byte.
        wrongKey = new String(new byte[] {'w', 'r', 'o', 'n', 'g'});
        testStringValue = provider.serialize(table, testStoreableValue);
    }

    @Test
    public void testDataFileConstructor() throws IOException, DatabaseCorruptedException {
        new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testDataFileThrowsExceptionCreatedForEmptyFile() throws IOException, DatabaseCorruptedException {
        filePath.getParent().toFile().mkdir();
        filePath.toFile().createNewFile();
        new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testDataFileThrowsExceptionWhenBadKeyFound() throws IOException, DatabaseCorruptedException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file
                     = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(wrongKey.getBytes(encoding));
            file.write('\0');
            file.writeInt(wrongKey.length() + 1 + offsetLength);
            file.write(testStringValue.getBytes(encoding));
        }
        new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
    }

    @Test(expected = DatabaseCorruptedException.class)
    public void testDataFileThrowsExceptionLoadedCorruptedFile() throws IOException, DatabaseCorruptedException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file
                     = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(correctKey1.getBytes(encoding));
            file.write('\0');
            file.writeInt(correctKey1.length() + 1 + offsetLength);
            //Value wasn't writed to the file.
        }
        new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
    }

    @Test
    public void testDataFileGetCommand() throws IOException, DatabaseCorruptedException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(correctKey1.getBytes(encoding));
            file.write('\0');
            file.writeInt(correctKey1.length() + correctKey2.length() + 2 + offsetLength * 2);
            file.write(correctKey2.getBytes(encoding));
            file.write('\0');
            file.writeInt(correctKey2.length() + correctKey2.length() + 2 + offsetLength * 2 + testStringValue.length());
            file.write(testStringValue.getBytes(encoding));
            file.write(testStringValue.getBytes(encoding));
        }
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertEquals(testStoreableValue, test.get(correctKey1));
        assertEquals(testStoreableValue, test.get(correctKey2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsExceptionForKeyNotFromThisDataFile()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.get(wrongKey);
    }

    @Test
    public void testGetCalledForNonAssociatedCorrectKey()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertEquals(null, test.get(correctKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionForKeyNotFromThisDataFile()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(wrongKey, testStoreableValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionForCorrectKeyAndNullValue()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(correctKey1, null);
    }

    @Test
    public void testPutReturnsNullForNewKey()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertEquals(null, test.put(correctKey1, testStoreableValue));
    }

    @Test
    public void testPutCalledTwiceForSameKey()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertEquals(null, test.put(correctKey1, testStoreableValue));
        assertEquals(testStoreableValue, test.put(correctKey1, testStoreableValue));
    }

    @Test
    public void testGetCalledForExistentKey()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(correctKey1, testStoreableValue);
        assertEquals(testStoreableValue, test.get(correctKey1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveThrowsExceptionForKeyNotFromThisDataFile()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.remove(wrongKey);
    }

    @Test
    public void testRemoveCalledForCorrectNonexistentKey()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertEquals(null, test.remove(correctKey1));
    }

    @Test
    public void testRemoveCalledForCorrectExistentKey()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(correctKey1, testStoreableValue);
        assertEquals(testStoreableValue, test.remove(correctKey1));
    }

    @Test
    public void testListCalledForEmptyDataFile() throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public void testPutKeysAndThenCallList()
            throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(correctKey1, testStoreableValue);
        test.put(correctKey2, testStoreableValue);
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(correctKey1);
        expectedKeySet.add(correctKey2);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(test.list());
        assertEquals(expectedKeySet, actualKeySet);
    }

    @Test
    public void testReadingExistentFileByCallingList() throws IOException, DatabaseCorruptedException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(correctKey1.getBytes(encoding));
            file.write('\0');
            file.writeInt(correctKey1.length() + correctKey2.length() + 2 + offsetLength * 2);
            file.write(correctKey2.getBytes(encoding));
            file.write('\0');
            file.writeInt(correctKey2.length() + correctKey2.length() + 2 + offsetLength * 2
                    + testStringValue.length());
            file.write(testStringValue.getBytes(encoding));
            file.write(testStringValue.getBytes(encoding));
        }
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(correctKey1);
        expectedKeySet.add(correctKey2);
        assertEquals(expectedKeySet, test.list());
    }

    @Test
    public void testWritingNewFileToDiskAndThenReadingIt() throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        String[] keyList = {correctKey1, correctKey2};
        test.put(keyList[0], testStoreableValue);
        test.put(keyList[1], testStoreableValue);
        test.commit();
        assertArrayEquals(keyList, test.list().toArray());
    }

    @Test
    public void testSavingEmptyDataFileToDisk() throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(correctKey1, testStoreableValue);
        test.remove(correctKey1);
        test.commit();
        test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        assertTrue(test.list().isEmpty());
    }

    @Test
    public void testSizeMethod() throws IOException, DatabaseCorruptedException {
        DataFile test = new DataFile(testDir, new Coordinates(folderIndex, fileIndex), table, provider);
        test.put(correctKey1, testStoreableValue);
        assertEquals(test.size(), 1);
        test.remove(correctKey1);
        assertEquals(test.size(), 0);
    }

    @After public void tearDown() {
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
