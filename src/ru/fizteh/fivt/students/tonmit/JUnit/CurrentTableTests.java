package ru.fizteh.fivt.students.tonmit.JUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CurrentTableTests {

    private final int dirNumber = 1;
    private final int fileNumber = 1;
    private final String encoding = "UTF-8";
    private File testDir = new File(System.getProperty("fizteh.db.dir"));
    private Table table;
    private Path filePath;

    @Before
    public void initTable() throws IOException {
        TableProviderFactory factory = new CurrentTableProviderFactory();
        filePath = Paths.get(testDir.toString(), dirNumber + ".dir",
                fileNumber + ".dat");
        TableProvider provider = factory.create(testDir.getCanonicalPath());
        table = provider.createTable("test");
    }

    @Test
    public void testGetName() {
        assertEquals("test", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void putNullKey() throws NullPointerException {
        table.put(null, "test");
    }

    @Test (expected = IllegalArgumentException.class)
    public void putNullValue() throws NullPointerException {
        table.put("test", null);
    }

    @Test
    public void testTablePartIsCreatedForCorrectFile() throws IOException, TableException {
        filePath.getParent().toFile().mkdir();
        String correctKey1 = "key1";
        String correctKey2 = "key2";
        String value = "value";

        try (DataOutputStream oStream
                     = new DataOutputStream(new FileOutputStream(filePath.toString()))) {
            byte[] keyInBytes = correctKey1.getBytes(encoding);
            byte[] valueInBytes = value.getBytes(encoding);
            oStream.writeInt(keyInBytes.length);
            oStream.write(keyInBytes);
            oStream.writeInt(valueInBytes.length);
            oStream.write(valueInBytes);

            keyInBytes = correctKey2.getBytes(encoding);
            oStream.writeInt(keyInBytes.length);
            oStream.write(keyInBytes);
            oStream.writeInt(valueInBytes.length);
            oStream.write(valueInBytes);
        }

        CurrentTable test = new CurrentTable(testDir.toPath());
        test.load("1");
        assertEquals(value, test.get(correctKey1));
        assertEquals(value, test.get(correctKey2));
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNull() {
        table.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeNull() {
        table.remove(null);
    }

    @Test
    public void testPutAndGet() {
        assertNull(table.put("a", "b"));
        assertEquals("b", table.get("a"));
        assertEquals("b", table.put("a", "c"));
        assertEquals("c", table.get("a"));
        assertEquals("c", table.put("a", "c"));
        assertEquals("c", table.get("a"));
        assertNull(table.get("qwerty"));
    }

    @Test
    public void testPutAndRemoveAndGet() {
        assertNull(table.put("a", "b"));
        assertNull(table.remove("qwerty"));
        assertEquals("b", table.remove("a"));
        assertNull(table.remove("a"));
        assertNull(table.get("a"));
    }

    @Test
    public void testSize() {
        assertEquals(0, table.size());
        table.put("1", "2");
        assertEquals(1, table.size());
        table.put("3", "4");
        assertEquals(2, table.size());
        table.put("3", "5");
        assertEquals(2, table.size());
        table.remove("3");
        assertEquals(1, table.size());
        table.remove("3");
        assertEquals(1, table.size());
        table.remove("1");
        assertEquals(0, table.size());
    }

    @Test
    public void testList() {
        assertEquals(0, table.list().size());
        table.put("1", "0");
        table.put("10", "0");
        table.put("10", "0");
        table.remove("1");
        table.put("2", "0");
        table.put("key", "value");
        assertEquals(3, table.list().size());
        assertTrue(table.list().containsAll(new ArrayList<>(Arrays.asList("10", "2", "key"))));
    }

    @Test
    public void testRollBack() {
        assertEquals(0, table.rollback());
        table.put("1", "1");
        table.put("2", "0");
        table.put("3", "0");
        table.remove("1");
        table.put("1", "1");
        table.put("1", "0");
        assertEquals(3, table.size());
        assertEquals(3, table.rollback());
        assertEquals(0, table.size());
        table.put("0", "0");
        table.remove("0");
        assertEquals(0, table.rollback());
    }

    @Test
    public void testCommit() {
        assertEquals(0, table.commit());
        table.put("1", "0");
        table.put("2", "0");
        table.put("3", "0");
        table.remove("3");
        assertEquals(2, table.commit());
    }

    @Test
    public void testCommitAndRollback() {
        table.put("1", "1");
        table.put("2", "2");
        table.put("3", "3");
        assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        assertNull(table.get("1"));
        assertNull(table.get("2"));
        assertEquals(2, table.rollback());
        assertEquals("1", table.get("1"));
        assertEquals("2", table.get("2"));
        table.remove("1");
        assertEquals(1, table.commit());
        assertEquals(2, table.size());
    }


    @After
    public void clearTestingDirectory() {
        for (File curDir : testDir.listFiles()) {
            for (File file : curDir.listFiles()) {
                file.delete();
            }
            curDir.delete();
        }
        testDir.delete();
    }
}
