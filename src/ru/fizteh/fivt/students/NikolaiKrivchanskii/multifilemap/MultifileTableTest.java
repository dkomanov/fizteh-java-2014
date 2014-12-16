package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.MyTable;

public class MultifileTableTest {

	private static final int KEYS_COUNT = 20;
    private static final String TABLE_NAME = "testtable";
    MyTable currentTable;

    TableProviderFactory factory = (TableProviderFactory) new DatabaseFactory();
    TableProvider provider = factory.create("C:\\temp\\database_test");
    

    @Before
    public void gettingReady() throws Exception {
        currentTable = (MyTable) provider.createTable(TABLE_NAME);
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }
    }

    @Test
    public void testForNewData() {
        // new data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String newKey = String.format("new_key%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertNull(currentTable.put(newKey, newValue));
        }
    }
    
    @Test
    public void testForExistingData() {
        // existing data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String expectedValue = String.format("value%d", index);
            String key = String.format("key%d", index);
            Assert.assertEquals(expectedValue, currentTable.get(key));
        }
    }

    @Test
    public void testForUnexistingData() {
        // non-existing data
        Random random = new Random();
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("k%d", random.nextInt(100));
            Assert.assertNull(currentTable.get(key));
        }
    }

    @Test
    public void testForReplaceData() {
        // replacing
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String oldValue = String.format("value%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertEquals(oldValue, currentTable.put(key, newValue));
        }
    }

    @Test
    public void testCommit() {
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
    public void testRollback() {
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
    public void testSize() {
        Assert.assertEquals(KEYS_COUNT, currentTable.size());
    }

    @Test
    public void testGetName() {
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
    public void testRollbackAndCommit()
    {
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

    @After
    public void cleaningUp() throws Exception {
        provider.removeTable(TABLE_NAME);
    }

}
