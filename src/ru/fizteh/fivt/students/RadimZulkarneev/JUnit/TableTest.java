package ru.fizteh.fivt.students.RadimZulkarneev.JUnit;

import static org.junit.Assert.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;

import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.FileMap;
import ru.fizteh.fivt.students.RadimZulkarneev.DataBase.TableRealize;

public class TableTest {

    private Path testDirectory;
    private final String tableName = "table";
    private TableRealize table = null;
    private final String incorrectFileName = "xa02.dat";
    private String dirName = "02";
    private String fileName = "07";
    private String key = "asd";
    private String value = "value";


    @Before
    public void setUp() {
        testDirectory = Paths.get(System.getProperty("java.io.tmpdir"), tableName);
        testDirectory.toFile().mkdir();
    }

    @Test
    public void tableCreatingNewTable() throws Exception {
        table = new TableRealize(testDirectory);
        assertEquals(tableName, table.getName());
    }

    @Test(expected = RuntimeException.class)
    public void tableInitializationWithNullDirectory() throws Exception {
        new TableRealize(null);
    }

    @Test(expected = RuntimeException.class)
    public void tableLoadFromIncorrectDirectory() throws Exception {
        testDirectory.resolve("dir").toFile().mkdir();
        try {
            new TableRealize(testDirectory);
        } catch (RuntimeException e) {
            testDirectory.resolve("dir").toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public void tableLoadFromFile() throws Exception {
        Files.createFile(testDirectory.resolve(incorrectFileName));
        try {
            new TableRealize(testDirectory.resolve(incorrectFileName));
        } catch (RuntimeException e) {
            testDirectory.resolve(incorrectFileName).toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public void tableLoadFromEmptyDirectory() throws Exception {
        testDirectory.resolve("02.dir").toFile().mkdir();
        try {
            new TableRealize(testDirectory);
        } catch (RuntimeException e) {
            testDirectory.resolve("02.dir").toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public void tableLoadFromDirectoryWithEmptyFiles() throws Exception {
        testDirectory.resolve("02.dir").toFile().mkdir();
        Files.createFile(testDirectory.resolve("02.dir").resolve("02.dat"));
        try {
            new TableRealize(testDirectory);
        } catch (RuntimeException e) {
            testDirectory.resolve("02.dir").resolve("02.dat").toFile().delete();
            testDirectory.resolve("02.dir").toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public void tableLoadFromDirectoryWithIncorrectNamedFiles() throws Exception {
        testDirectory.resolve("02.dir").toFile().mkdir();
        Files.createFile(testDirectory.resolve("02.dir").resolve("a2.dat"));
        try {
            new TableRealize(testDirectory);
        } catch (RuntimeException e) {
            testDirectory.resolve("02.dir").resolve("a2.dat").toFile().delete();
            testDirectory.resolve("02.dir").toFile().delete();
            throw new RuntimeException();
        }
    }

    @Test(expected = RuntimeException.class)
    public void tableLoadFromNonExistingDirectory() throws Exception {
        Path currentTestDirectory = testDirectory.resolve("current").resolve("current");
        table = new TableRealize(currentTestDirectory);
    }

    @Test
    public void tableLoadFromDirectoryWhichContainsCorrectFiles() throws Exception {
        Path subDirectory = testDirectory.resolve(dirName + ".dir");
        subDirectory.toFile().mkdir();
        Path filePath = subDirectory.resolve(fileName + ".dat");
        FileMap fileMap = new FileMap(filePath);
        fileMap.put(key, value);
        fileMap.close();
        table = new TableRealize(testDirectory);
        assertEquals(1, table.size());
    }

    @Test
    public void tablePutAndCommitFunctionTest() throws Exception {
        table = new TableRealize(testDirectory);
        int size = table.size();
        assertNull(table.put(key, value));
        table.commit();
        table = new TableRealize(testDirectory);
        assertEquals(size, table.size() - 1);
        assertEquals(value, table.put(key, value));
        assertNull(table.put(key + "1", value));
        table.commit();
        table = new TableRealize(testDirectory);
        assertEquals(size, table.size() - 2);
        String newValue = value + "x";
        assertEquals(value, table.put(key, newValue));
        assertEquals(newValue, table.put(key, value));
        assertEquals(value, table.remove(key));
        assertEquals(value, table.remove(key + "1"));
        table.commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tablePutNullParametrs() throws Exception {
        table = new TableRealize(testDirectory);
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tableRemoveNullParametrs() throws Exception {
        table = new TableRealize(testDirectory);
        table.remove(null);
    }

    @Test
    public void tableRemoveFunctionTest() throws Exception {
        table = new TableRealize(testDirectory);
        assertNull(table.remove(key));
        table.put(key, value);
        assertEquals(value, table.remove(key));
        table.put(key, value);
        table.commit();
        table = new TableRealize(testDirectory);
        assertEquals(value, table.remove(key));
    }

    @Test
    public void tableGetFunctionTest() throws Exception {
        table = new TableRealize(testDirectory);
        assertNull(table.get(key + "g"));
        table.put(key, value);
        assertEquals(value, table.get(key));
        assertEquals(1, table.commit());
        table = new TableRealize(testDirectory);
        assertEquals(value, table.get(key));
    }

    @Test
    public void tableListFunctionTest() throws Exception {
        table = new TableRealize(testDirectory);
        table.put(key, value);
        table.put(key + "1", value);
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(key, key + "1"));
        assertTrue(list.containsAll(table.list()));
        table.remove(key);
        table.commit();
        table = new TableRealize(testDirectory);
        table.put(key, value);
        assertTrue(list.containsAll(table.list()));
        table.remove(key + "1");
        list.remove(key + "1");
        assertTrue(list.containsAll(table.list()));
        assertEquals(1, table.size());
    }

    @Test
    public void tableRollbackFunctionTest() throws Exception {
        table = new TableRealize(testDirectory);
        table.put(key, value);
        table.put(key + "1", value);
        assertEquals(2, table.size());
        assertEquals(2, table.getUncommitedChanges());
        assertEquals(2, table.rollback());
        assertEquals(0, table.size());
    }

    @After
    public void tearDown() throws Exception {
        if (table != null) {
            table.drop();
        }
        testDirectory.toFile().delete();
    }


}
