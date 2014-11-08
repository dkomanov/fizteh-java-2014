/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.tests.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBaseProviderFactory;
import static org.junit.Assert.*;

/**
 *
 * @author shakarim
 */
public class MultiFileTableTest {

    private final String tableName = "TestTable";
    private final int size = 30;
    private TableProviderFactory factory;
    private TableProvider provider;
    private Table instance;
    private String testDirectory;

    @Before
    public void setUp() {
        testDirectory = System.getProperty("java.io.tmpdir");
        factory = new DataBaseProviderFactory();
        provider = factory.create(testDirectory);
        instance = provider.createTable(tableName);

        for (int order = 0; order < size; ++order) {
            String key = getKey(order);
            String value = getValue(order);
            instance.put(key, value);
        }
    }

    @After
    public void tearDown() {
        provider.removeTable(tableName);
    }

    /**
     * Test of getName method, of class MultiFileTable.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String result = instance.getName();
        assertEquals(tableName, result);
    }

    /**
     * Test of get method, of class MultiFileTable.
     */
    @Test
    public void testGet() {
        System.out.println("get");

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

    /**
     * Test of put method, of class MultiFileTable.
     */
    @Test
    public void testPut() {
        System.out.println("put");
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

    /**
     * Test of remove method, of class MultiFileTable.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
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

    /**
     * Test of size method, of class MultiFileTable.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        int result = instance.size();
        assertEquals(size, result);
    }

    /**
     * Test of commit method, of class MultiFileTable.
     */
    @Test
    public void testCommit() {
        System.out.println("commit");
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

    /**
     * Test of rollback method, of class MultiFileTable.
     */
    @Test
    public void testRollback() {
        System.out.println("rollback");
        int expResult = size;
        int result = instance.rollback();
        assertEquals(expResult, result);
    }

    /**
     * Test of list method, of class MultiFileTable.
     */
    @Test
    public void testList() {
        System.out.println("list");
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
        System.out.println("get null");
        instance.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
        System.out.println("put null");
        instance.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        System.out.println("remove null");
        instance.remove(null);
    }

    private String getKey(int order) {
        return String.format("key%d", order);
    }

    private String getValue(int order) {
        return String.format("value%d", order);
    }
}
