package ru.fizteh.fivt.students.standy66_new.tests.task4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Some JUnit tests on Table interface.
 * Created by andrew on 01.11.14.
 */
public class TableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    StringTable testTable;
    File testDirectory;

    @Before
    public void setUp() throws Exception {
        testDirectory = folder.newFolder("table");
        testTable = new StringTable(testDirectory, null);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("table", testTable.getName());
    }

    @Test
    public void testGetOrdinaryKey() throws Exception {
        testTable.put("key", "value");
        assertEquals("value", testTable.get("key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithException() throws Exception {
        testTable.get(null);
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        assertEquals(null, testTable.get("notExistingKey"));
    }

    @Test
    public void testPutNewKeyAndValue() throws Exception {
        assertEquals(null, testTable.put("key", "value"));
    }

    @Test
    public void testPutWithOverwritingOldValue() throws Exception {
        testTable.put("key", "value1");
        assertEquals("value1", testTable.put("key", "value2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKeyWithNotNullValue() throws Exception {
        testTable.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKeyWithNullValue() throws Exception {
        testTable.put(null, null);
    }

    @Test
    public void testRemoveExistingKey() throws Exception {
        testTable.put("key", "value");
        assertEquals("value", testTable.remove("key"));
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        assertEquals(null, testTable.remove("notExistingKey"));
    }

    @Test
    public void testSize() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        assertEquals(2, testTable.size());
    }

    @Test
    public void testCommit() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        testTable.remove("key1");
        assertEquals(2, testTable.commit());
    }

    @Test
    public void testCommitedKeys() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        testTable.remove("key1");
        testTable.commit();
        StringTable tableAfterCommit = new StringTable(testDirectory, null);
        assertNotEquals(null, tableAfterCommit.get("key2"));
    }

    @Test
    public void testRollback() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        testTable.remove("key1");
        assertEquals(2, testTable.rollback());
    }

    @Test
    public void testRevertedKeys() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        testTable.remove("key1");
        testTable.rollback();
        StringTable tableAfterRollback = new StringTable(testDirectory, null);
        assertEquals(null, tableAfterRollback.get("key2"));
    }

    @Test
    public void testList() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        Collections.sort(keys);
        List<String> realKeys = testTable.list();
        Collections.sort(realKeys);
        assertEquals(keys, realKeys);
    }
}
