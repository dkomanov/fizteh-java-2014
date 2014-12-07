package ru.fizteh.fivt.students.pershik.JUnit.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.students.pershik.JUnit.HashTable;
import ru.fizteh.fivt.students.pershik.JUnit.HashTableProvider;
import ru.fizteh.fivt.students.pershik.JUnit.HashTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pershik on 10/30/14.
 */
public class HashTableTest {
    private static HashTableProviderFactory factory;
    private static HashTableProvider provider;
    private static HashTable table;
    private static String tableName = "table";

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new HashTableProviderFactory();
    }

    @Before
    public void before() throws IOException {
        provider = factory.create(tmpFolder.newFolder().getAbsolutePath());
        table = provider.createTable(tableName);
    }

    @After
    public void after() {
        provider.removeTable(tableName);
    }

    @Test
    public void readWrite() {
        table.put("key", "value");
        table.put("ключ", "значение");
        table.commit();
        HashTable newTable = provider.getTable("table");
        Assert.assertEquals("value", newTable.get("key"));
        Assert.assertEquals("значение", newTable.get("ключ"));
    }

    @Test
    public void getName() {
        Assert.assertEquals("table", table.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        table.get(null);
    }

    @Test
    public void getNotExisting() {
        Assert.assertNull(table.get("key"));
    }

    @Test
    public void getExisting() {
        table.put("key", "value");
        Assert.assertEquals(table.get("key"), "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullValue() {
        table.put("key", null);
    }

    @Test
    public void putNotExisting() {
        Assert.assertEquals(table.put("key", "value"), null);
    }

    @Test
    public void putExisting() {
        table.put("key", "old_value");
        Assert.assertEquals("old_value", table.put("key", "new_value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        table.remove(null);
    }

    @Test
    public void removeNotExisting() {
        Assert.assertNull(table.remove("key"));
    }

    @Test
    public void removeExisting() {
        table.put("key", "value");
        Assert.assertEquals("value", table.remove("key"));
    }

    @Test
    public void size() {
        Assert.assertEquals(0, table.size());
        table.put("1", "1");
        table.put("2", "2");
        Assert.assertEquals(2, table.size());
        table.put("1", "3");
        Assert.assertEquals(2, table.size());
        table.remove("1");
        Assert.assertEquals(1, table.size());
    }

    @Test
    public void commitNothing() {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void commit() {
        table.put("1", "1");
        table.put("2", "2");
        table.size();
        table.remove("2");
        table.remove("tt");
        Assert.assertEquals(3, table.commit());
    }

    @Test
    public void rollback() {
        table.put("1", "1");
        table.put("2", "2");
        Assert.assertEquals(2, table.commit());
        table.put("2", "3");
        table.put("4", "4");
        Assert.assertEquals(2, table.rollback());
        Assert.assertNull(table.get("4"));
        Assert.assertEquals("2", table.get("2"));
    }

    @Test
    public void listNotEmpty() {
        table.put("1", "1");
        table.put("2", "2");
        table.put("3", "3");
        List list = table.list();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.containsAll(
                new ArrayList<>(Arrays.asList("1", "2", "3"))));
    }

    @Test
    public void listEmpty() {
        Assert.assertEquals(0, table.list().size());
    }
}
