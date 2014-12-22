package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;


public class MyTableTest {
    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "TestDir");
    private final String tableName = "table1"; 
    private final int dirNumber = 1;
    private final int fileNumber = 1; 
    private final String subDir =  "1.dir";
    private final String subFile = "1.dat";
    private String correctKey;
    private final String testKey = "testKey";
    private final String anotherKey = "anotherKey";
    private final String testValue = "testValue";
    private final int offsetLength = 4;
    
    @Before
    public void setUp() {
        testDir.toFile().mkdir(); 
        byte[] b = {dirNumber + fileNumber * 16, 'k', 'e', 'y'};
        correctKey = new String(b);
    }

    @Test
    public void creatingNewTableTest() throws DataBaseException {
        new MyTable(testDir, tableName);
    } 
    
    @Test
    public void getNameTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(tableName, test.getName());
    }
    
    @Test
    public void loadingExistedTableTest() throws IOException, DataBaseException {
        Path subdirectoryPath = testDir.resolve(subDir);
        subdirectoryPath.toFile().mkdir();
        Path subfilePath = subdirectoryPath.resolve(subFile);
        try (DataOutputStream file
                = new DataOutputStream(new FileOutputStream(subfilePath.toString()))) {
            file.write(correctKey.getBytes("UTF-8"));
            file.write('\0');
            file.writeInt(correctKey.length() + 1 + offsetLength);
            file.write(testValue.getBytes("UTF-8"));
        }
        Table testTable = new MyTable(testDir, tableName);
        assertEquals(testValue, testTable.get(testKey));
    }

    @Test
    public void commitTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        test.put(testKey, testValue);
        test.commit();
        test = new MyTable(testDir, tableName);
        assertEquals(testValue, test.get(testKey));
    }
    @Test
    public void putNewTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
    }
    
    @Test
    public void putOldTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(testValue, test.put(testKey, testValue));
    }
    
    @Test
    public void putOldwithCommitTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.put(testKey, testValue));
    } 

    @Test
    public void getExistedTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(testValue, test.get(testKey));
    }
    
    @Test(expected = NullPointerException.class)
    public void getExistedwithCommitTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.get(testKey));
    }    

    @Test
    public void getNOExistedwithCommitTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.get(testKey));
    }
    
    @Test
    public void removingExistedTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(testValue, test.remove(testKey));
    }
    
    @Test
    public void removingExistedwithCommitTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        test.commit();
        assertEquals(testValue, test.remove(testKey));
    }    

    @Test
    public void emtyListTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertTrue(test.list().isEmpty());
    }
    
    @Test
    public void noEmtyListTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
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
    public void emtySizeTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(0, test.size());
    }
    
    @Test
    public void noEmtySizeTest() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
        assertEquals(null, test.put(testKey, testValue));
        assertEquals(null, test.put(anotherKey, testValue));
        assertEquals(testValue, test.remove(anotherKey));
        assertEquals(1, test.size());
    }    

    @Test
    public void testCommitEmptiedAfterLoadingTable() throws DataBaseException {
        Table test = new MyTable(testDir, tableName);
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
    public void removeDat() {
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
