package ru.fizteh.fivt.students.standy66_new.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.students.standy66_new.exceptions.FileCorruptedException;
import ru.fizteh.fivt.students.standy66_new.storage.FileMap;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Some JUnit tests on FileMap.
 * Created by andrew on 01.11.14.
 */
public class FileMapTest {
    private FileMap fileMap;
    private File fileMapFile;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        fileMapFile = tempFolder.newFile();
        fileMap = new FileMap(fileMapFile);
    }

    @Test
    public void testConstructorIntegrity() throws Exception {
        commitAndReopen();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullShouldFail() throws Exception {
        fileMap = new FileMap(null);
    }

    @Test
    public void testPut() throws Exception {
        int loaded = bulkLoad();
        assertEquals(loaded, commitAndReopen());
        assertEquals("b", fileMap.get("a"));
        assertEquals("ололо", fileMap.get("Эгегей"));
    }

    @Test
    public void testRemove() throws Exception {
        int loaded = bulkLoad();
        assertEquals(loaded, commitAndReopen());
        assertEquals("b", fileMap.remove("a"));
        assertNull(fileMap.get("a"));
        assertEquals(1, rollbackAndReopen());
    }

    @Test
    public void testRemoveNull() throws Exception {
        assertNull(fileMap.remove(null));
    }

    @Test
    public void testGet() throws Exception {
        bulkLoad();
        commitAndReopen();
        assertEquals("b", fileMap.get("a"));
        assertEquals("d", fileMap.get("c"));
        assertEquals("ололо", fileMap.get("Эгегей"));
    }

    @Test
    public void testRollback() throws Exception {
        bulkLoad();
        commitAndReopen();
        assertEquals("b", fileMap.get("a"));
        fileMap.put("a", "c");
        fileMap.put("123", "123");
        assertEquals(2, rollbackAndReopen());
        assertNull(fileMap.get("123"));
        assertEquals("b", fileMap.get("a"));
    }

    @Test(expected = FileCorruptedException.class)
    public void testIncorrectFile1() throws Exception {
        PrintWriter pw = new PrintWriter(fileMapFile);
        pw.println("This line shines according to file format");
        pw.close();
        fileMap = new FileMap(fileMapFile);
    }

    @Test(expected = FileCorruptedException.class)
    public void testIncorrectFile2() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileMapFile)) {
            fos.write(new byte[]{ 0, 0, 0, 1, 97, 127, 127, 127, 127 });
        }
        fileMap = new FileMap(fileMapFile);
    }

    @Test(expected = FileCorruptedException.class)
    public void testIncorrectFile3() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileMapFile)) {
            fos.write(new byte[]{ 0, 0, 0, 1, 97, -127, -127, -127, -127 });
        }
        fileMap = new FileMap(fileMapFile);
    }

    @Test(expected = FileCorruptedException.class)
    public void testIncorrectFile4() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileMapFile)) {
            fos.write(new byte[]{ 0, 0, 0, 1, 97, 0 });
        }
        fileMap = new FileMap(fileMapFile);
    }

    @Test
    public void testClear() throws Exception {
        bulkLoad();
        commitAndReopen();
        fileMap.clear();
        commitAndReopen();
        assertEquals(0, fileMap.size());
        assertTrue(fileMap.isEmpty());
    }

    @Test
    public void testSize() throws Exception {
        int loaded = bulkLoad();
        assertEquals(loaded, fileMap.size());
        commitAndReopen();
        assertEquals(loaded, fileMap.size());
        fileMap.remove("a");
        fileMap.remove("c");
        assertEquals(loaded - 2, fileMap.size());
        rollbackAndReopen();
        assertEquals(loaded, fileMap.size());
    }

    @Test
    public void testContains() throws Exception {
        bulkLoad();
        commitAndReopen();
        assertTrue(fileMap.containsKey("a"));
        assertTrue(fileMap.containsKey("c"));
        assertTrue(fileMap.containsValue("ололо"));
        assertFalse(fileMap.containsKey("asdkjhalksdjhlaskjd"));
        assertFalse(fileMap.containsValue("asdkjhalksdjhlaskjd"));

    }

    @Test
    public void testAutoclosable() throws Exception {
        try (FileMap fm = new FileMap(fileMapFile)) {
            fm.put("a", "b");
        }
        fileMap = new FileMap(fileMapFile);
        assertEquals("b", fileMap.get("a"));

    }

    private int commitAndReopen() throws Exception {
        int changed = fileMap.commit();
        fileMap = new FileMap(fileMapFile);
        return changed;
    }

    private int rollbackAndReopen() throws Exception {
        int changed = fileMap.rollback();
        fileMap = new FileMap(fileMapFile);
        return changed;
    }

    private int bulkLoad() throws Exception {
        String[][] data = new String[][]{
                {"a", "b"},
                {"c", "d"},
                {"e", "f"},
                {"more complicated", "example"},
                {"Эгегей", "ололо"}
        };
        for (String[] keyValue : data) {
            fileMap.put(keyValue[0], keyValue[1]);
        }
        return data.length;
    }
}
