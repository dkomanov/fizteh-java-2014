package ru.fizteh.fivt.students.alexpodkin.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.alexpodkin.JUnit.HashTableProviderFactory;

import java.io.IOException;

/**
 * Created by Alex on 16.11.14.
 */
public class HashTableProviderTest {
    private static TableProvider tableProvider;
    private static TableProviderFactory tableProviderFactory;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        tableProviderFactory = new HashTableProviderFactory();
    }

    @Before
    public void before() throws IOException {
        tableProvider = tableProviderFactory.create(tmpFolder.newFolder().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullName() {
        tableProvider.createTable(null);
    }

    @Test
    public void createNotExistTable() {
        Assert.assertNotNull(tableProvider.createTable("table"));
    }

    @Test
    public void createExistTable() {
        tableProvider.createTable("table");
        Assert.assertNull(tableProvider.createTable("table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInvalidName() {
        tableProvider.createTable("..");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullName() {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNotExistTable() {
        tableProvider.removeTable("table");
    }

    @Test
    public void remove() {
        tableProvider.createTable("table");
        tableProvider.removeTable("table");
        Assert.assertNotNull(tableProvider.createTable("table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableName() {
        tableProvider.getTable(null);
    }

    @Test
    public void getNotExistTable() {
        Assert.assertNull(tableProvider.getTable("table"));
    }

    @Test
    public void get() {
        tableProvider.createTable("table");
        Assert.assertNotNull(tableProvider.getTable("table"));
    }

}
