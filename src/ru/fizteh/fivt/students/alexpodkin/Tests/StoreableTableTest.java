package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.alexpodkin.Storeable.StoreableEntry;
import ru.fizteh.fivt.students.alexpodkin.Storeable.StoreableTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 25.11.14.
 */
public class StoreableTableTest {
    private static TableProviderFactory tableProviderFactory;
    private static TableProvider tableProvider;
    private static Table table;
    private static List<Class<?>> signature;
    private Storeable storeable;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        tableProviderFactory = new StoreableTableProviderFactory();
        signature = new ArrayList<>();
        signature.add(String.class);
    }

    @Before
    public void before() throws IOException {
        tableProvider = tableProviderFactory.create(temporaryFolder.newFolder().getAbsolutePath());
        table = tableProvider.createTable("table", signature);
        storeable = tableProvider.createFor(table);
    }

    @After
    public void after() throws IOException {
        tableProvider.createTable("table", signature);
        tableProvider.removeTable("table");
    }

    @Test
    public void readAndWriteTest() throws IOException {
        storeable.setColumnAt(0, "v");
        table.put("k", storeable);
        table.commit();
        Table storeableTable = tableProvider.getTable("table");
        Assert.assertEquals("v", storeableTable.get("k").getStringAt(0));
    }

    @Test(expected = ColumnFormatException.class)
    public void putWithColumnExceptionTest() {
        List<Class<?>> signature = new ArrayList<>();
        signature.add(Integer.class);
        StoreableEntry incorrectStoreable = new StoreableEntry(signature);
        incorrectStoreable.setColumnAt(0, 0);
        table.put("k", incorrectStoreable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putWithNullKeyTest() {
        storeable.setColumnAt(0, "v");
        table.put(null, storeable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putWithNullValueTest() {
        table.put("k", null);
    }

    @Test
    public void putExistsElement() {
        storeable.setColumnAt(0, "v");
        table.put("k", storeable);
        StoreableEntry storeable1 = new StoreableEntry(signature);
        storeable1.setColumnAt(0, "v1");
        Assert.assertEquals("v", table.put("k", storeable1).getStringAt(0));
    }

    @Test
    public void putNotExistsElement() {
        storeable.setColumnAt(0, "v");
        Assert.assertEquals(null, table.put("k", storeable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullElementTest() {
        Assert.assertNull(table.remove(null));
    }

    @Test
    public void removeNotExistsElementTest() {
        Assert.assertNull(table.remove("k"));
    }

    @Test
    public void removeExistsElementTest() {
        storeable.setColumnAt(0, "k");
        table.put("k", storeable);
        Assert.assertEquals("k", table.remove("k").getStringAt(0));
    }

    @Test
    public void sizeTest() {
        Assert.assertEquals(0, table.size());
        storeable.setColumnAt(0, "1");
        table.put("k", storeable);
        Assert.assertEquals(1, table.size());
        table.remove("k");
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void emptyCommitTest() throws IOException {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void commitTest() throws IOException {
        storeable.setColumnAt(0, "1");
        table.put("k", storeable);
        Assert.assertEquals(1, table.commit());
    }

    @Test
    public void rollbackTest() {
        storeable.setColumnAt(0, "1");
        table.put("k", storeable);
        Assert.assertEquals(1, table.rollback());
    }

    @Test
    public void getColumnCountTest() {
        Assert.assertEquals(table.getColumnsCount(), 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnTypeWithExceptionTest() {
        table.getColumnType(1);
    }

    @Test
    public void getColumnTypeTest() {
        Assert.assertEquals(String.class, table.getColumnType(0));
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals("table", table.getName());
    }

    @Test
    public void getTest() {
        storeable.setColumnAt(0, "k");
        table.put("k", storeable);
        Assert.assertEquals("k", table.get("k").getStringAt(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getWithExceptionTest() {
        table.get(null);
    }

    @Test
    public void getNumberOfUncommittedChangesTest() {
        storeable.setColumnAt(0, "k");
        table.put("k", storeable);
        Assert.assertEquals(1, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void listTest() {
        storeable.setColumnAt(0, "k");
        table.put("k", storeable);
        Assert.assertEquals("k", table.list().get(0));
    }
}
