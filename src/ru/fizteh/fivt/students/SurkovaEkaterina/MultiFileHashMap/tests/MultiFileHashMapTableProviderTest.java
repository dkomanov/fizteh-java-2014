package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapTableProviderFactory;

public class MultiFileHashMapTableProviderTest {
    MultiFileHashMapTableProviderFactory factory;
    MultiFileHashMapTableProvider provider;

    @Before
    public void beforeTest() {
        factory = new MultiFileHashMapTableProviderFactory();
        provider = factory.create("test");
        provider.createTable("table1");
        provider.createTable("table2");
    }

    @Test
    public void testGetTable() throws Exception {
        Assert.assertNull(provider.getTable("nonexistingtable"));
        Assert.assertNull(provider.getTable("thereisnosuchtable"));

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
        Assert.assertNotNull(provider.createTable("newtable1"));
        Assert.assertNotNull(provider.createTable("newtable2"));

        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNull(provider.createTable("table2"));

        provider.removeTable("newtable1");
        provider.removeTable("newtable2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() {
        provider.createTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
        provider.createTable("newtable1");
        provider.createTable("newtable2");

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
