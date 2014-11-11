package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luba_yaronskaya on 09.11.14.
 */
@RunWith(Parameterized.class)
public class TableTest {
    private TableProviderFactory factory;
    private Table table;
    static int dataSize = 10000;
    static String[] keys = new String[dataSize];
    static String[] values = new String[dataSize];
    static String[][] specialData = new String[][]{
            {"russian", "буковки"},
            {"русские", "буковки"},
            {"\t", "\n"},
            {"null", "null"},
            {"\\", "/"}
    };
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setKeysAndValues() {
        for (int i = 0; i < dataSize; ++i) {
            keys[i] = "key" + i;
            values[i] = "value" + i;
        }
    }

    @Before
    public void setUpTable() throws IOException {
        TableProvider provider = factory.create(testFolder.newFolder().getCanonicalPath());
        table = provider.createTable("test");
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new JUnitTableProviderFactory()}
        });
    }

    public TableTest(TableProviderFactory factory) {
        this.factory = factory;
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("test", table.getName());
    }

    @Test
    public void testGetExistentKey() throws Exception {
        table.put("key", "value");
        assertEquals("value", table.get("key"));
        for (int i = 0; i < dataSize; ++i) {
            assertNull(table.put(keys[i], values[i]));
        }
        for (int i = 0; i < dataSize; ++i) {
            assertNotNull(table.get(keys[i]));
        }
    }

    @Test
    public void testGetNonexistentKey() throws Exception {
        assertNull(table.get("nonexistent key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put("key", null);
    }

    @Test
    public void testPutSpecialKey() throws Exception {
        for (int i = 0; i < specialData.length; ++i) {
            assertNull(table.put(specialData[i][0], specialData[i][1]));
        }
        for (int i = 0; i < specialData.length; ++i) {
            assertNotNull(table.get(specialData[i][0]));
        }
    }

    @Test
    public void testPutNewKey() throws Exception {
        assertNull(table.put("new key", "value"));
    }

    @Test
    public void testPutExistentKey() throws Exception {
        table.put("key", "value1");
        assertEquals("value1", table.put("key", "value2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullKey() throws Exception {
        table.remove(null);
    }

    @Test
    public void testRemoveExistentKey() throws Exception {
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], values[i]);
            assertEquals(values[i], table.remove(keys[i]));
            assertNull(table.get(keys[i]));
        }
    }

    @Test
    public void testRemoveNonexistentKey() throws Exception {
        assertNull(table.get("nonexistent"));
    }

    @Test
    public void testSizeIsEmpty() throws Exception {
        assertEquals(0, table.size());
    }

    @Test
    public void testSize() throws Exception {
        for (int i = 0; i < dataSize / 2; ++i) {
            table.put(keys[i], values[i]);
        }
        assertEquals(dataSize / 2, table.size());
    }

    @Test
    public void testCommit() throws Exception {
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], values[i]);
        }
        assertEquals(dataSize, table.commit());
        for (int i = 0; i < dataSize; ++i) {
            assertNotNull(values[i], table.get(keys[i]));
        }
        for (int i = 0; i < dataSize - 1; ++i) {
            table.put(keys[i], values[i + 1]);
        }
        for (int i = 0; i < dataSize; ++i) {
            assertNotNull(values[i], table.get(keys[i]));
        }
        assertEquals(dataSize - 1, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], values[i]);
        }
        assertEquals(dataSize, table.rollback());
        assertEquals(0, table.rollback());
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], values[i]);
        }
        assertEquals(dataSize, table.commit());
        for (int i = 0; i < dataSize / 2; ++i) {
            table.put(keys[i], values[dataSize / 2 + i]);
        }
        assertEquals(dataSize / 2, table.rollback());
        assertEquals(0, table.rollback());
    }
}
