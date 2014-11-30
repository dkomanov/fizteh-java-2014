package ru.fizteh.fivt.students.torunova.junit.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.torunova.junit.TableImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableTest {
    TableImpl testTable;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Before
    public void setUp() throws Exception {
        File testDirectory = folder.newFolder("table");
        testTable = new TableImpl(testDirectory.getAbsolutePath());
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
    public void testPutWithOwerwritingOldValue() throws Exception {
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
    @Test(expected = IllegalArgumentException.class)
    public void testPutNotNullKeyWithNullValue() throws Exception {
        testTable.put("key", null);
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
        assertEquals(1, testTable.commit());
    }

    @Test
    public void testRollback() throws Exception {
        testTable.put("key1", "value1");
        testTable.put("key2", "value2");
        testTable.remove("key1");
        assertEquals(1, testTable.rollback());
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
