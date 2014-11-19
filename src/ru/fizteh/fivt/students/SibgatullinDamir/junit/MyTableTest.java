package ru.fizteh.fivt.students.SibgatullinDamir.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class MyTableTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public Table table;
    public String path;

    @Before
    public void initTable() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        path = tempFolder.newFolder().getAbsolutePath();
        TableProvider provider = factory.create(path);
        table = provider.createTable("my_table");
    }

    @After
    public void removeTable() throws IOException {
        DatabaseJUnit.database.clear();
    }

    @Test
    public void testGetName() {
        assertEquals("my_table", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void putNull() {
        table.put(null, "a");
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
    public void testPutAndGet() {
        assertNull(table.put("1", "2"));
        assertEquals("2", table.get("1"));
        assertEquals("2", table.put("1", "3"));
        assertEquals("3", table.get("1"));
        assertNull(table.get("a"));
    }

    @Test
    public void testPutAndRemove() {
        assertNull(table.put("1", "2"));
        assertNull(table.remove("2"));
        assertEquals("2", table.remove("1"));
        assertNull(table.remove("1"));
        assertNull(table.get("1"));
    }

    @Test
    public void testSize() {
        assertEquals(0, table.size());
        table.put("1", "2");
        assertEquals(1, table.size());
        table.put("3", "4");
        assertEquals(2, table.size());
        table.put("3", "5");
        assertEquals(2, table.size());
        table.remove("1");
        assertEquals(1, table.size());
        table.remove("1");
        assertEquals(1, table.size());
        table.remove("3");
        assertEquals(0, table.size());
    }

    @Test
    public void testList() {
        assertEquals(0, table.list().size());
        table.put("1", "2");
        table.put("3", "4");
        table.put("3", "5");
        table.remove("1");
        table.put("6", "7");
        table.put("key", "value");
        assertEquals(3, table.list().size());
        assertTrue(table.list().containsAll(new LinkedList<>(Arrays.asList("3", "6", "key"))));
    }

    @Test
    public void testCommit() {
        assertEquals(0, table.commit());
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        table.remove("3");
        assertEquals(4, table.commit());
        assertEquals(2, table.size());
        TableProviderFactory factory = new MyTableProviderFactory();
        TableProvider provider = factory.create(path);
        Table sameTable = provider.getTable("table");
        assertEquals(2, sameTable.size());
    }

    @Test
    public void testRollBack() {
        assertEquals(0, table.rollback());
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        table.remove("1");
        table.put("1", "5");
        assertEquals(3, table.size());
        assertEquals(5, table.rollback());
        assertEquals(0, table.size());
    }

    @Test
    public void testCommitAndRollback() {
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        assertNull(table.get("1"));
        assertNull(table.get("2"));
        assertEquals(2, table.rollback());
        assertEquals("2", table.get("1"));
        assertEquals("3", table.get("2"));
        table.remove("1");
        assertEquals(1, table.commit());
        assertEquals(2, table.size());
        table.put("1", "2");
        assertEquals(3, table.size());
        assertEquals(1, table.rollback());
        assertEquals(2, table.size());
    }
}
