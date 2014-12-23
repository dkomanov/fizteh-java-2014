package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProvider;

public class MyTableProviderTest {

    private final Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    private final String dirName = "test";
        private final Path dirPath = testDir.resolve(dirName);
        private final String testTableName = "testTable";
        
        @Before
        public void setUp() {
            testDir.toFile().mkdir();
        }
    
        @Test
        public void createNewDirTableProviderTest() throws DataBaseException {
            new MyTableProvider(dirPath.toString());
        }
        
        @Test
        public void createOldDirTableProviderTest() throws DataBaseException {
            dirPath.toFile().mkdir();
            new MyTableProvider(dirPath.toString());
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void pathisnotDirTest() throws IOException, DataBaseException {
           dirPath.toFile().createNewFile();
            new MyTableProvider(dirPath.toString());
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void ivalidPathTest() throws DataBaseException {
            new MyTableProvider("\0");
        }
        
        @Test
        public void creatingTableInsideDirTest()
                throws IOException, DataBaseException {
            dirPath.toFile().mkdir();
            dirPath.resolve(testTableName).toFile().mkdir();
            TableProvider test = new MyTableProvider(dirPath.toString());
            assertNotEquals(null, test.getTable(testTableName));
        }
       
        @Test(expected = IllegalArgumentException.class)
        public void createNullTableTest() throws DataBaseException {
            TableProvider test = new MyTableProvider(dirPath.toString());
            test.createTable(null);
        }
        
       @Test(expected = IllegalArgumentException.class)
        public void getNullTableTest() throws Exception {
            TableProvider test = new MyTableProvider(dirPath.toString());
            test.getTable(null);
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void removeNullTableTest() throws Exception {
            TableProvider test = new MyTableProvider(dirPath.toString());
            test.removeTable(null);
        }
        
        @Test(expected = NullPointerException.class)
        public void createTableTest() throws Exception {
            TableProvider test = new MyTableProvider(dirPath.toString());
            assertEquals(null, test.createTable(testTableName));
        }
        
        @Test(expected = Exception.class)
        public void createtwoTableTest() throws Exception {
            TableProvider test = new MyTableProvider(dirPath.toString());
            assertEquals(null, test.createTable(testTableName));
            test.createTable(testTableName);
        }
        
        @Test(expected = Exception.class)
        public void removeTableTest() throws Exception {
            TableProvider test = new MyTableProvider(dirPath.toString());
            assertEquals(null, test.createTable(testTableName));
            test.removeTable(testTableName);
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
