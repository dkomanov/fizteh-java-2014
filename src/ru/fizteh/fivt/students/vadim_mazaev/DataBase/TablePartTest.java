package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import static org.junit.Assert.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TablePartTest {
    private final int offsetLength = 4;
    private final int dirNumber = 1;
    private final int fileNumber = 1;
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    private final Path filePath = Paths.get(testDir.toString(), dirNumber + ".dir",
            fileNumber + ".dat");
    private String wrongKey;
    private String correctKey1;
    private String correctKey2;
    private final String testValue = "val";
    
    @Before
    public void setUp() {
        testDir.toFile().mkdir();
        //Each key should be placed to directory with number 
        //key.getBytes()[0] % NUMBER_OF_PARTITIONS
        //and file with number
        //(key.getBytes()[0] / NUMBER_OF_PARTITIONS) % NUMBER_OF_PARTITIONS.
        byte[] b = {dirNumber + fileNumber * TablePart.NUMBER_OF_PARTITIONS, 'k', 'e', 'y', '1'};
        correctKey1 = new String(b);
        b[4] = '2';
        correctKey2 = new String(b);
        //Wrong key will contain changed first byte.
        b[0]++;
        wrongKey = new String(b);
    }
    
    @Test
    public void testTablePartIsCreatedForNonexistentFile() throws DataBaseIOException {
        new TablePart(testDir, dirNumber, fileNumber);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testTablePartThrowsExceptionCreatedForEmptyFile()
            throws DataBaseIOException, IOException {
        filePath.getParent().toFile().mkdir();
        filePath.toFile().createNewFile();
        new TablePart(testDir, dirNumber, fileNumber);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testTablePartThrowsExceptionLoadedFileHasKeyFromAnotherBucket()
            throws IOException, DataBaseIOException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file
                = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(wrongKey.getBytes(TablePart.CODING));
            file.write('\0');
            file.writeInt(wrongKey.length() + 1 + offsetLength);
            file.write(testValue.getBytes(TablePart.CODING));
        }
        new TablePart(testDir, dirNumber, fileNumber);
    }
    
    @Test(expected = DataBaseIOException.class)
    public void testTablePartThrowsExceptionLoadedCorruptedFile()
            throws IOException, DataBaseIOException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file
                = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(correctKey1.getBytes(TablePart.CODING));
            file.write('\0');
            file.writeInt(correctKey1.length() + 1 + offsetLength);
            //Value wasn't writed to the file.
        }
        new TablePart(testDir, dirNumber, fileNumber);
    }
    
    @Test
    public void testTablePartIsCreatedForCorrectFile() throws IOException, DataBaseIOException {
        filePath.getParent().toFile().mkdir();
        try (DataOutputStream file
                = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(correctKey1.getBytes(TablePart.CODING));
            file.write('\0');
            file.writeInt(correctKey1.length() + correctKey2.length() + 2 + offsetLength * 2);
            file.write(correctKey2.getBytes(TablePart.CODING));
            file.write('\0');
            file.writeInt(correctKey2.length() + correctKey2.length() + 2 + offsetLength * 2
                    + testValue.length());
            file.write(testValue.getBytes(TablePart.CODING));
            file.write(testValue.getBytes(TablePart.CODING));
        }
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertEquals(testValue, test.get(correctKey1));
        assertEquals(testValue, test.get(correctKey2));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetThrowsExceptionForKeyNotFromThisTablePart()
            throws UnsupportedEncodingException, DataBaseIOException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.get(wrongKey);
    }
    
    @Test
    public void testGetCalledForNonAssociatedCorrectKey()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertEquals(null, test.get(correctKey1));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionForKeyNotFromThisTablePart()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.put(wrongKey, testValue);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutThrowsExceptionForCorrectKeyAndNullValue()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.put(correctKey1, null);
    }
    
    @Test
    public void testPutCalledForNewCorrectKeyAndNonNullValue()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertEquals(null, test.put(correctKey1, testValue));
    }
    
    @Test
    public void testPutCalledTwiceForSameKey()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertEquals(null, test.put(correctKey1, testValue));
        assertEquals(testValue, test.put(correctKey1, testValue));
    }
    
    @Test
    public void testGetCalledForExistentKey()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.put(correctKey1, testValue);
        assertEquals(testValue, test.get(correctKey1));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveThrowsExceptionForKeyNotFromThisTablePart()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.remove(wrongKey);
    }
    
    @Test
    public void testRemoveCalledForCorrectNonexistentKey()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertEquals(null, test.remove(correctKey1));
    }
    
    @Test
    public void testRemoveCalledForCorrectExistentKey()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.put(correctKey1, testValue);
        assertEquals(testValue, test.remove(correctKey1));
    }
    
    @Test
    public void testListCalledForEmptyTablePart() throws DataBaseIOException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertTrue(test.list().isEmpty());
    }
    
    @Test
    public void testListCalledForNonEmptyTablePart()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.put(correctKey1, testValue);
        test.put(correctKey2, testValue);
        Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add(correctKey1);
        expectedKeySet.add(correctKey2);
        Set<String> actualKeySet = new HashSet<>();
        actualKeySet.addAll(test.list());
        assertEquals(expectedKeySet, actualKeySet);
    }
    
    @Test
    public void testReadingExistentFileByCallingList() throws IOException, DataBaseIOException {
        filePath.getParent().toFile().mkdir();
        String[] keyList = {correctKey1, correctKey2};
        try (DataOutputStream file
                = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            file.write(correctKey1.getBytes(TablePart.CODING));
            file.write('\0');
            file.writeInt(correctKey1.length() + correctKey2.length() + 2 + offsetLength * 2);
            file.write(correctKey2.getBytes(TablePart.CODING));
            file.write('\0');
            file.writeInt(correctKey2.length() + correctKey2.length() + 2 + offsetLength * 2
                    + testValue.length());
            file.write(testValue.getBytes(TablePart.CODING));
            file.write(testValue.getBytes(TablePart.CODING));
        }
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        assertArrayEquals(keyList, test.list().toArray());
    }
    
    @Test
    public void testWritingNewFileToDiskAndThenReadingIt()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        String[] keyList = {correctKey1, correctKey2};
        test.put(keyList[0], testValue);
        test.put(keyList[1], testValue);
        test.commit();
        assertArrayEquals(keyList, test.list().toArray());
    }
    
    @Test
    public void testSavingEmptyTablePartToDisk()
            throws DataBaseIOException, UnsupportedEncodingException {
        TablePart test = new TablePart(testDir, dirNumber, fileNumber);
        test.put(correctKey1, testValue);
        test.remove(correctKey1);
        test.commit();
        test = new TablePart(testDir, dirNumber, fileNumber);
        assertTrue(test.list().isEmpty());
    }
    
    @After public void tearDown() {
        for (File curDir : testDir.toFile().listFiles()) {
            for (File file : curDir.listFiles()) {
                file.delete();
            }
            curDir.delete();
        }
        testDir.toFile().delete();
    }
}
