package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.alexpodkin.JUnit.HashTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashTableTest {

    private static Table table;
    private static TableProvider tableProvider;
    private static HashTableProviderFactory tableProviderFactory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        tableProviderFactory = new HashTableProviderFactory();
    }

    @Before
    public void before() throws IOException {
        tableProvider = tableProviderFactory.create(temporaryFolder.newFolder().getAbsolutePath());
        table = tableProvider.createTable("table");
    }

    @After
    public void after() {
        tableProvider.removeTable("table");
    }

    @Test
    public void commit() {
        table.put("k1", "v1");
        table.put("k2", "v2");
        Assert.assertEquals(2, table.commit());
        Table hashTable = tableProvider.getTable("table");
        Assert.assertEquals("v1", hashTable.get("k1"));
        Assert.assertEquals("v2", hashTable.get("k2"));
    }

    @Test
    public void rollback() {
        table.put("k1", "v1");
        Assert.assertEquals(1, table.commit());
        table.put("k2", "v2");
        table.rollback();
        Table hashTable = tableProvider.getTable("table");
        Assert.assertEquals(1, hashTable.size());
    }

    @Test
    public void size() {
        Assert.assertEquals(0, table.size());
        table.put("k1", "v1");
        Assert.assertEquals(1, table.size());
        table.put("k2", "v2");
        Assert.assertEquals(2, table.size());
    }

    @Test
    public void listNotEmpty() {
        table.put("k1", "v1");
        table.put("k2", "v2");
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add("k1");
        checkList.add("k2");
        List<String> actualList = table.list();
        Assert.assertEquals(checkList.get(0), actualList.get(0));
        Assert.assertEquals(checkList.get(1), actualList.get(1));
    }

    @Test
    public void listEmpty() {
        Assert.assertEquals(0, table.list().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeWithNullKey() {
        table.remove(null);
    }

    @Test
    public void removeNotExistElement() {
        Assert.assertNull(table.remove("k1"));
    }

    @Test
    public void removeExistElement() {
        table.put("k1", "v1");
        Assert.assertEquals("v1", table.remove("k1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putWithNullKey() {
        table.put(null, "v1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putWithNullValue() {
        table.put("k1", null);
    }

    @Test
    public void putNotExistElement() {
        Assert.assertNull(table.put("k1", "v1"));
    }

    @Test
    public void putExistElement() {
        table.put("k1", "v1");
        Assert.assertEquals("v1", table.put("k1", "v2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        table.get(null);
    }

    @Test
    public void getElement() {
        table.put("k1", "v1");
        Assert.assertEquals("v1", table.get("k1"));
    }

    @Test
    public void getName() {
        Assert.assertEquals("table", table.getName());
    }

}
