package ru.fizteh.fivt.students.hromov_igor.multifilemap.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProviderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DBaseTableTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public String path;

    public Table table;

    @Before
    public void initTable() throws Exception {
        TableProviderFactory factory = new DBProviderFactory();
        path = folder.newFolder("test").getAbsolutePath();
        TableProvider provider = factory.create(path);
        table = provider.createTable("table");
        if (table == null) {
            table = provider.getTable("table");
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void putNull() throws Exception {
        table.put(null, null);
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
    public void testPutAndGet() throws Exception {
        table.put("1", "2");
        assertEquals("2", table.get("1"));
        table.put("1", "3");
        table.commit();
        assertEquals("3", table.get("1"));
    }

    @Test
    public void testPutAndRemove() throws Exception {
        table.put("1", "2");
        table.remove("1");
        assertNull(table.get("1"));
    }

    @Test
    public void testSize() throws Exception {
        table.remove("1");
        table.remove("3");
        table.put("1", "2");
        table.put("3", "4");
        table.put("3", "5");
        assertEquals(3 , table.size());
        table.remove("1");
        table.remove("1");
        table.remove("3");
        assertEquals(0, table.size());
    }

    @Test
    public void testRollBack() throws Exception {
        assertEquals(0, table.rollback());
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        table.remove("1");
        table.put("1", "5");
        assertEquals(5, table.rollback());
        assertEquals("not found", table.get("1"));
        assertEquals("not found", table.get("2"));
        assertEquals("not found", table.get("3"));
    }

    @Test
    public void testCommit() throws Exception {
        assertEquals(0, table.commit());
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        table.remove("3");
        assertEquals(4, table.commit());
    }

    @Test
    public void testCommitAndRollback() throws Exception {
        table.put("1", "a");
        table.put("2", "b");
        assertEquals(2, table.commit());
        table.remove("1");
        table.remove("2");
        assertEquals(2, table.commit());
        assertNull(table.get("1"));
        assertNull(table.get("2"));
        assertEquals(2, table.rollback());
        assertEquals("2", table.get("1"));
        table.remove("1");
        assertEquals(1, table.commit());
        assertEquals(2, table.size());
        table.put("1", "2");
        assertEquals(2, table.size());
        assertEquals(null, table.get("1"));
    }
}
