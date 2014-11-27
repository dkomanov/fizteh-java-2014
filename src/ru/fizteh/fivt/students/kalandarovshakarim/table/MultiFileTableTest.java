/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.table;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.*;
import static org.junit.Assert.*;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBaseProviderFactory;

/**
 *
 * @author shakarim
 */
public class MultiFileTableTest {

    private static final String TABLE_NAME = "TestTable";
    private final int size = 30;
    private TableProviderFactory factory;
    private TableProvider provider;
    private Table instance;
    private String testDirectory;

    @Before
    public void setUp() {
        testDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "db.dir").toString();
        factory = new DataBaseProviderFactory();
        provider = factory.create(testDirectory);
        instance = provider.createTable(TABLE_NAME);

        for (int order = 0; order < size; ++order) {
            String key = getKey(order);
            String value = getValue(order);
            instance.put(key, value);
        }
    }

    @After
    public void tearDown() {
        provider.removeTable(TABLE_NAME);
    }

    @Test
    public void testGetName() {
        String result = instance.getName();
        assertEquals(TABLE_NAME, result);
    }

    @Test
    public void testGet() {
        for (int order = 0; order < size; ++order) {
            String key = getKey(order);
            String keyNotExists = getKey(order + size);
            String expResult = getValue(order);
            String result = instance.get(key);
            String nullResult = instance.get(keyNotExists);
            assertEquals(expResult, result); // Tests found.
            assertNull(nullResult); // Tests not found.
        }
    }

    @Test
    public void testPut() {
        for (int order = 0; order < size; ++order) {
            // Tests overwrite.
            String key = getKey(order);
            String value = getValue(order + size);
            String result = instance.put(key, value);
            String expResult = getValue(order);
            assertEquals(expResult, result);
            // Tests new.
            String newKey = getKey(order + size);
            String nullResult = instance.put(newKey, value);
            assertNull(nullResult);
        }
    }

    @Test
    public void testRemove() {
        for (int order = 0; order < size; ++order) {
            // Tests removed.
            String key = getKey(order);
            String result = instance.remove(key);
            String expResult = getValue(order);
            assertEquals(expResult, result);
            // Tests not found.
            String newKey = getKey(order + size);
            String nullResult = instance.remove(newKey);
            assertNull(nullResult);
        }
    }

    @Test
    public void testSize() {
        int result = instance.size();
        assertEquals(size, result);
    }

    @Test
    public void testCommit() {
        int expResult = size;
        int result = instance.commit();
        assertEquals(expResult, result);

        String key = "ключ";
        String value = "значение";
        assertCommit(key, value, 1);
        assertCommit(key, value, 0);
        instance.remove(key);
        assertCommit(key, value, 0);

        instance.put("новый_ключ", "новое_значение");
        instance.remove("новый_ключ");
        expResult = 0;
        result = instance.commit();
        assertEquals(expResult, result);
    }

    private void assertCommit(String key, String value, int expResult) {
        instance.put(key, value);
        int result = instance.commit();
        assertEquals(expResult, result);
    }

    @Test
    public void testRollback() {
        int expResult = size;
        assertNotNull(instance.get(getKey(0)));
        int result = instance.rollback();
        assertEquals(expResult, result);
        assertNull(instance.get(getKey(0)));
    }

    @Test
    public void testList() {
        List<String> expResult = new ArrayList<>();
        for (int key = 0; key < size; ++key) {
            expResult.add(String.format("key%d", key));
        }
        List<String> result = instance.list();
        Collections.sort(result);
        Collections.sort(expResult);
        assertEquals(expResult, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        instance.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
        instance.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        instance.remove(null);
    }

    private String getKey(int order) {
        return String.format("key%d", order);
    }

    private String getValue(int order) {
        return String.format("value%d", order);
    }
}
