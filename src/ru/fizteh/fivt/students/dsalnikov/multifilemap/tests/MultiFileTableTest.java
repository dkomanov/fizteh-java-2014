package ru.fizteh.fivt.students.dsalnikov.multifilemap.tests;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.DBFactory;

import java.util.Random;

public class MultiFileTableTest {
    private static final int KEYS_COUNT = 20;
    private static final String TABLE_NAME = "testtable";

    TableProviderFactory factory = new DBFactory();
    TableProvider provider = factory.create("C:\\temp\\database_test");
    Table currentTable;

    @Before
    public void setUp() throws Exception {
        currentTable = provider.createTable(TABLE_NAME);
        prepareData();
    }

    @After
    public void afterTest() throws Exception {
        provider.removeTable(TABLE_NAME);
    }

    @Test
    public void testTableExistingData() {
        // existing data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String expectedValue = String.format("value%d", index);
            String key = String.format("key%d", index);
            Assert.assertEquals(expectedValue, currentTable.get(key));
        }
    }

    @Test
    public void testTableNonExistingData() {
        // non-existing data
        Random random = new Random();
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("k%d", random.nextInt(100));
            Assert.assertNull(currentTable.get(key));
        }
    }

    @Test
    public void testTableNewData() {
        // new data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String newKey = String.format("new_key%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertNull(currentTable.put(newKey, newValue));
        }
    }

    @Test
    public void testTableReplaceData() {
        // replacing
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String oldValue = String.format("value%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertEquals(oldValue, currentTable.put(key, newValue));
        }
    }

    @Test
    public void testTableCommit() {
        int committed = currentTable.commit();
        Assert.assertEquals(KEYS_COUNT, committed);

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }

        Assert.assertEquals(KEYS_COUNT, currentTable.commit());

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            Assert.assertNotNull(currentTable.get(key));
        }
    }

    @Test
    public void testTableRollback() {
        Assert.assertEquals(KEYS_COUNT, currentTable.rollback());

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }

        Assert.assertEquals(2 * KEYS_COUNT, currentTable.rollback());

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            Assert.assertNull(currentTable.get(key));
        }
    }

    @Test
    public void testTableSize() {
        Assert.assertEquals(KEYS_COUNT, currentTable.size());
    }

    @Test
    public void testTableGetName() {
        Assert.assertEquals(TABLE_NAME, currentTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableExceptions() {
        // get
        currentTable.get(null);

        // storagePut
        currentTable.put(null, "value");
        currentTable.put("key", null);

        // storageRemove
        currentTable.remove(null);
    }

    @Test
    public void testRollbackCommit() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }
        currentTable.commit();
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            currentTable.remove(key);
        }
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }
        Assert.assertEquals(0, currentTable.rollback());

        currentTable.remove("non-exists");
        currentTable.remove("non-exists1");
        currentTable.remove("key1");
        currentTable.put("key1", "value1");
        Assert.assertEquals(0, currentTable.rollback());

        currentTable.put("key1", "value1");
        currentTable.commit();
        currentTable.remove("key1");
        currentTable.put("key1", "value1");
        Assert.assertEquals(0, currentTable.rollback());
    }

    private void prepareData() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }
    }
}
