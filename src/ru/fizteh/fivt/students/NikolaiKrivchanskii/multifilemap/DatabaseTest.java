package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.SomethingIsWrongException;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.MyTable;

public class DatabaseTest {

    DatabaseFactory factory;
    TableProvider provider;

    @Before
    public void beforeTest() throws SomethingIsWrongException {
        factory = new DatabaseFactory();
        provider = factory.create("C:\\temp\\database_test");
        provider.createTable("table1");
        provider.createTable("table2");
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

    @Test
    public void testGetTable() throws Exception {
        // non-existing tables
        Assert.assertNull(provider.getTable("nonexistingtable"));
        Assert.assertNull(provider.getTable("thereisnosuchtable"));
        // existing tables
        Assert.assertNotNull(provider.getTable("table1"));
        Assert.assertNotNull(provider.getTable("table2"));

        MyTable table1 = (MyTable) provider.getTable("table1");
        Assert.assertEquals(table1, provider.getTable("table1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableExceptions() throws SomethingIsWrongException {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() throws SomethingIsWrongException {
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
    public void testRemoveTableIllegalArgumentException() throws SomethingIsWrongException {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIllegalStateException() throws SomethingIsWrongException {
        provider.removeTable("nonexistingtable");
        provider.removeTable("nosuchtable");
    }
    
    @After
    public void testAfter() throws SomethingIsWrongException {
        provider.removeTable("table1");
        provider.removeTable("table2");
    }
}
