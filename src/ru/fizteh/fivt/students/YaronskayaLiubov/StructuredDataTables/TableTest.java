package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by luba_yaronskaya on 17.11.14.
 */
@RunWith(Parameterized.class)
public class TableTest {
    private TableProviderFactory factory;
    private Table table;
    static List<Class<?>> columnTypes;
    static List<Class<?>> columnTypes2;
    static Class<?>[] availableTypes = {Integer.class, Long.class,
            Byte.class, Float.class, Double.class, Boolean.class, String.class};
    static int dataSize = 1000;
    static String[] keys = new String[dataSize];
    static Storeable row;
    static Storeable[] rows = new Storeable[dataSize];
    static String[][] specialData = new String[][]{
            {"russian", "буковки"},
            {"русские", "буковки"},
            {"\t", "\n"},
            {"null", "null"},
            {"\\", "/"}
    };
    static Storeable[] specialRows = new Storeable[specialData.length];
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setColumnTypes() {
        columnTypes = new ArrayList<>();
        columnTypes = Arrays.asList(availableTypes);
        columnTypes2 = new ArrayList<>();
        columnTypes2.add(availableTypes[0]);
        columnTypes2.add(availableTypes[6]);
    }

    @Before
    public void setUpTable() throws IOException {
        TableProvider provider = factory.create(testFolder.newFolder().getCanonicalPath());
        table = provider.createTable("test", columnTypes2);
        row = provider.createFor(table);
        row.setColumnAt(0, 1);
        row.setColumnAt(1, "value");
        for (int i = 0; i < dataSize; ++i) {
            keys[i] = "key" + i;
            rows[i] = provider.createFor(table);
            rows[i].setColumnAt(0, i);
            rows[i].setColumnAt(1, "value" + i);
        }
        for (int i = 0; i < specialData.length; ++i) {
            specialRows[i] = provider.createFor(table);
            specialRows[i].setColumnAt(0, i);
            specialRows[i].setColumnAt(1, specialData[i][1]);
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new StoreableDataTableProviderFactory()}
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
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], rows[i]);
        }
        for (int i = 0; i < dataSize; ++i) {
            for (int i1 = 0; i1 < table.getColumnsCount(); ++i1) {
                assertEquals(rows[i].getColumnAt(i1), table.get(keys[i]).getColumnAt(i1));
            }
        }
        for (int i = 0; i < dataSize; ++i) {
            assertNotNull(table.put(keys[i], rows[i]));
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
        table.put(null, row);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put("key", null);
    }

    @Test
    public void testPutSpecialKey() throws Exception {
        for (int i = 0; i < specialData.length; ++i) {
            assertNull(table.put(specialData[i][0], specialRows[i]));
        }
        for (int i = 0; i < specialData.length; ++i) {
            assertNotNull(table.get(specialData[i][0]));
        }
    }

    @Test
    public void testPutNewKey() throws Exception {
        assertNull(table.put("new key", row));
    }

    @Test
    public void testPutExistentKey() throws Exception {
        table.put("key", row);
        Storeable oldRow = table.put("key", rows[0]);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            assertEquals(row.getColumnAt(i), oldRow.getColumnAt(i));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullKey() throws Exception {
        table.remove(null);
    }

    @Test
    public void testRemoveExistentKey() throws Exception {
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], rows[i]);
            Storeable oldRow = table.remove(keys[i]);
            for (int i1 = 0; i1 < table.getColumnsCount(); ++i1) {
                assertEquals(rows[i].getColumnAt(i1), oldRow.getColumnAt(i1));
            }
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
            table.put(keys[i], rows[i]);
        }
        assertEquals(dataSize / 2, table.size());
    }

    @Test
    public void testCommit() throws Exception {
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], rows[i]);
        }
        assertEquals(dataSize, table.commit());
        for (int i = 0; i < dataSize; ++i) {
            Storeable oldRow = table.get(keys[i]);
            for (int i1 = 0; i1 < table.getColumnsCount(); ++i1) {
                assertEquals(rows[i].getColumnAt(i1), oldRow.getColumnAt(i1));
            }
        }
        for (int i = 0; i < dataSize - 1; ++i) {
            table.put(keys[i], rows[i + 1]);
        }
        for (int i = 0; i < dataSize - 1; ++i) {
            Storeable oldRow = table.get(keys[i]);
            for (int i1 = 0; i1 < table.getColumnsCount(); ++i1) {
                assertEquals(rows[i + 1].getColumnAt(i1), oldRow.getColumnAt(i1));
            }
        }
        assertEquals(dataSize - 1, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], rows[i]);
        }
        assertEquals(dataSize, table.rollback());
        assertEquals(0, table.rollback());
        for (int i = 0; i < dataSize; ++i) {
            table.put(keys[i], rows[i]);
        }
        assertEquals(dataSize, table.commit());
        for (int i = 0; i < dataSize / 2; ++i) {
            table.put(keys[i], rows[dataSize / 2 + i]);
        }
        assertEquals(dataSize / 2, table.rollback());
        assertEquals(0, table.rollback());
    }
}
