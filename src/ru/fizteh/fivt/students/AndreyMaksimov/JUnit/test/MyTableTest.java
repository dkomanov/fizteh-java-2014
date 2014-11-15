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
        assertNull(table.put("111", "333"));
        assertEquals("333", table.get("111"));
        assertEquals("333", table.put("111", "555"));
        assertEquals("555", table.get("111"));
        assertNull(table.get("fskhfsjkfsdlkjfklsd"));
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
    }

    @Test
    public void testList() {
        assertEquals(0, table.list().size());
        table.put("100000", "200000");
        table.put("300000", "400000");
        table.put("300000", "500000");
        table.remove("100000");
        table.put("600000", "700000");
        table.put("key", "value");
        assertEquals(3, table.list().size());
        assertTrue(table.list().containsAll(new LinkedList<>(Arrays.asList("300000", "600000", "key"))));
    }

    @Test
    public void testRollBack() {
        assertEquals(0, table.rollback());
        table.put("10", "20");
        table.put("20", "30");
        table.put("30", "40");
        table.remove("10");
        assertEquals(2, table.size());
        assertEquals(4, table.rollback());
        assertEquals(0, table.size());
    }

    @Test
    public void testCommit() {
        assertEquals(0, table.commit());
        table.put("100", "200");
        table.put("200", "300");
        table.put("300", "400");
        table.remove("300");
        assertEquals(4, table.commit());
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
        assertEquals(2, table.rollback());

    }
}
