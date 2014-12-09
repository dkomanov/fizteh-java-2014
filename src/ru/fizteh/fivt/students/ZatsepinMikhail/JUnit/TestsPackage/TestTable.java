package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.TestsPackage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage.MFileHashMapFactory;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class TestTable {
    String key;
    String value;
    String newValue;
    String providerDirectory;
    String tableName;

    Table testTable;
    TableProvider provider;
    TableProviderFactory factory;

    @Before
    public void setUp() {
        key = "key";
        value = "value";
        newValue = "newvalue";
        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable";
        factory = new MFileHashMapFactory();
        provider = factory.create(providerDirectory);
        testTable = provider.createTable(tableName);
    }

    @After
    public void tearDown() {
        provider.removeTable(tableName);
    }

    @Test
    public void testGetName() throws Exception {
        assertTrue(testTable.getName().equals(tableName));
    }

    @Test
    public void testGet() throws Exception {
        assertNull(testTable.get(key));
        testTable.put(key, value);
        assertTrue(testTable.get(key).equals(value));
        testTable.put(key, newValue);
        assertTrue(testTable.get(key).equals(newValue));
        testTable.remove(key);
        assertNull(testTable.get(key));
        testTable.rollback();

        testTable.put(key, value);
        testTable.commit();
        testTable.put(key, newValue);
        assertTrue(testTable.get(key).equals(newValue));
        testTable.remove(key);
        assertNull(testTable.get(key));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(testTable.remove(key));
        testTable.put(key, value);
        assertTrue(testTable.remove(key).equals(value));

        testTable.rollback();
        testTable.put(key, value);
        assertTrue(testTable.remove(key).equals(value));
        testTable.put(key, value);
        testTable.commit();
        assertTrue(testTable.remove(key).equals(value));
        assertNull(testTable.remove(key));
    }

    @Test
    public void testPut() throws Exception {
        assertNull(testTable.put(key, value));
        assertTrue(testTable.put(key, value).equals(value));
        assertTrue(testTable.get(key).equals(value));
        assertTrue(testTable.put(key, newValue).equals(value));
        assertTrue(testTable.get(key).equals(newValue));
        testTable.remove(key);
        assertNull(testTable.put(key, value));

        String keyForCommit = "keyCM";
        String valueForCommit = "vakueCM";
        int size = 5;
        for (int i = 0; i < size; ++i) {
            assertNull(testTable.put(keyForCommit + i, valueForCommit + i));
        }
        testTable.commit();
        for (int i = 0; i < size; ++i) {
            assertTrue(testTable.get(keyForCommit + i).equals(valueForCommit + i));
        }

        String freshValue = "addValue";
        testTable.rollback();
        testTable.remove(keyForCommit + 1);
        assertNull(testTable.put(keyForCommit + 1, valueForCommit + 1));
        assertTrue(testTable.put(keyForCommit + 2, freshValue).equals(valueForCommit + 2));
        assertTrue(testTable.put(keyForCommit + 2, freshValue + "*").equals(freshValue));
    }

    @Test
    public void testSize() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            testTable.put(key + i, value + i);
        }
        assertTrue(testTable.size() == size);
        testTable.remove(key + 0);
        assertTrue(testTable.size() == size - 1);
    }

    @Test
    public void testRollback() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            testTable.put(key + i, value + i);
        }
        testTable.remove(key + 0);
        testTable.remove(key + 2);
        testTable.put(key + 1, newValue);
        assertTrue(testTable.rollback() == size - 2);
        testTable.put(key, value);
        assertTrue(testTable.rollback() == 1);
        testTable.put(key, value);
        testTable.commit();
        assertTrue(testTable.rollback() == 0);
    }

    @Test
    public void testCommit() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            testTable.put(key + i, value + i);
        }
        testTable.remove(key + 0);
        testTable.remove(key + 2);
        testTable.put(key + 1, newValue);
        assertTrue(testTable.commit() == size - 2);
        assertTrue(testTable.rollback() == 0);
        testTable.put(key, newValue);
        testTable.remove(key);
        assertTrue(testTable.commit() == 0);
        testTable.put(key + 1, newValue + 2);
        assertTrue(testTable.commit() == 1);
    }

    @Test
    public void testList() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            testTable.put(key + i, value + i);
        }
        boolean theSame = true;
        List<String> testList = testTable.list();
        for (int i = 0; i < size; ++i) {
            if (!testList.contains(key + i)) {
                theSame = false;
            }
        }
        testTable.remove(key + 4);
        testTable.remove(key + 3);
        testList = testTable.list();
        for (int i = 0; i < size - 2; ++i) {
            if (!testList.contains(key + i)) {
                theSame = false;
            }
        }
        assertTrue(theSame);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInput() {
        testTable.get(null);
        testTable.remove(null);
        testTable.put(null, value);
        testTable.put(key, null);
    }
}
