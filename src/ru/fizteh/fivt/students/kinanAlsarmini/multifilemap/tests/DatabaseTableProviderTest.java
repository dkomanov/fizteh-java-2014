package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.tests;

import org.junit.Before;
import org.junit.Assert;
import org.junit.*;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.DatabaseFactory;

public class DatabaseTableProviderTest {
    TableProviderFactory factory;
    TableProvider provider;

    @Before
    public void beforeTest() {
        factory = new DatabaseFactory();
        provider = factory.create("/home/database_test");
    }

    @Test
    public void testGetTable() throws Exception {
        // non-existing tables
        Assert.assertNull(provider.getTable("nonexistingtable"));
        Assert.assertNull(provider.getTable("thereisnosuchtable"));
        // existing tables
        Assert.assertNotNull(provider.getTable("table1"));
        Assert.assertNotNull(provider.getTable("table2"));

        Table table1 = provider.getTable("table1");
        Assert.assertEquals(table1, provider.getTable("table1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableExceptions() {
        provider.getTable(null);
    }

    @Test
    public void testCreateTable() throws Exception {
        // non-existing tables
        Assert.assertNotNull(provider.createTable("newtable1"));
        Assert.assertNotNull(provider.createTable("newtable2"));
        // existing tables
        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNull(provider.createTable("table2"));

        // clean-up
        provider.removeTable("newtable1");
        provider.removeTable("newtable2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() {
        provider.createTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
        //prepare
        provider.createTable("newtable1");
        provider.createTable("newtable2");

        // existing tables
        provider.removeTable("newtable1");
        provider.removeTable("newtable2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalArgumentException() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIllegalStateException() {
        provider.removeTable("nonexistingtable");
        provider.removeTable("nosuchtable");
    }
}
