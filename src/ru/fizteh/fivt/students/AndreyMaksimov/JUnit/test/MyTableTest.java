package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MyTableProviderFactory;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.strings.Table;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.strings.TableProvider;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.strings.TableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class MyTableTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public Table table;
    public String dbDirPath;

    @Before
    public void initTable() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        dbDirPath = tmpFolder.newFolder().getAbsolutePath();
        TableProvider provider = factory.create(dbDirPath);
        table = provider.createTable("table");
    }

    @Test
    public void testGetName() {
        assertEquals("table", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void putNull() {
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
    public void testPutAndGet() {
        assertNull(table.put("a", "b"));
        assertEquals("b", table.get("a"));
        assertEquals("b", table.put("a", "c"));
        assertEquals("c", table.get("a"));
        assertNull(table.get("h"));
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
    }

    @Test
    public void testList() {
        assertEquals(0, table.list().size());
        table.put("1000000000000", "2000000000000");
        table.put("3000000000000", "4000000000000");
        table.put("3000000000000", "5000000000000");
        table.remove("1000000000000");
        table.put("6000000000000", "7000000000000");
        table.put("key", "value");
        assertEquals(3, table.list().size());
        assertTrue(table.list().containsAll(new LinkedList<>(Arrays.asList("3000000000000", "6000000000000", "key"))));
    }

    @Test
    public void testRollBack() {
        assertEquals(0, table.rollback());
        table.put("100", "200");
        table.put("200", "300");
        table.put("300", "400");
        table.remove("100");
        table.put("100", "500");
        assertEquals(3, table.size());
        assertEquals(3, table.rollback());
        assertEquals(0, table.size());
    }

    @Test
    public void testCommit() {
        assertEquals(0, table.commit());
        table.put("100", "200");
        table.put("200", "300");
        table.put("300", "400");
        table.remove("300");
        assertEquals(2, table.commit());
        assertEquals(2, table.size());
        TableProviderFactory factory = new MyTableProviderFactory();
        TableProvider provider = factory.create(dbDirPath);
        Table sameTable = provider.getTable("table");
        assertEquals(2, sameTable.size());
    }

    @Test
    public void testCommitAndRollback() {
        table.put("10000000", "20000000");
        table.put("20000000", "30000000");
        table.put("30000000", "40000000");
        assertEquals(3, table.commit());
        table.remove("10000000");
        table.remove("20000000");
        assertNull(table.get("10000000"));
        assertNull(table.get("20000000"));
        assertEquals(2, table.rollback());
    }
}
