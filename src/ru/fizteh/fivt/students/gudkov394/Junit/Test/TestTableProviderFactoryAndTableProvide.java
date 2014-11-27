package ru.fizteh.fivt.students.gudkov394.Junit.Test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.gudkov394.Junit.src.Junit;


/**
 *
 * Created by kagudkov on 20.10.14.
 */
public class TestTableProviderFactoryAndTableProvide {
    TableProvider provider;
    TableProviderFactory factory;

    @Before
    public void beforeTest() {
        factory = new Junit();
        provider = factory.create("/home/kagudkov/fizteh-java-2014/test1");
        provider.createTable("table1");
        provider.createTable("table2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionDirIsEqualsNull() {
        Junit factory = new Junit();
        factory.create(null);
    }

    @Test
    public void testGetTable() throws Exception {
// non-existing tables
        Assert.assertNull(provider.getTable("NonExistingTable"));
        Assert.assertNull(provider.getTable("ThereIsNoSuchTable"));
// existing tables
        Assert.assertNotNull(provider.getTable("table1"));
        Assert.assertNotNull(provider.getTable("table2"));
        Table table1 = provider.getTable("table1");
        Assert.assertEquals(table1, provider.getTable("table1"));
    }
    @Test
    public void testCreateTable() throws Exception {
// non-existing table
        Assert.assertNotNull(provider.createTable("newTable1"));
        Table table = provider.getTable("newTable1");
        table.put("1", "2");
        table.commit();
// existing tables
        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNull(provider.createTable("table2"));
// clean-up
        provider.removeTable("newTable1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() {
        provider.createTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
//prepare
        provider.createTable("newTable1");
        Table table = provider.getTable("newTable1");
        table.put("1", "2");
        table.commit();
// existing tables
        provider.removeTable("newTable1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalArgumentException() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIllegalStateException() {
        provider.removeTable("nonExistingTable");
        provider.removeTable("nosuchtable");
    }
}
