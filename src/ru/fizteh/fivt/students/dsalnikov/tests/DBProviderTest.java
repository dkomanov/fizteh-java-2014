package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.DBFactory;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.DBTableProvider;

import java.io.IOException;

public class DBProviderTest {
    DBTableProvider tableProvider;

    @Before
    public void setUpTestObject() throws IOException {
        tableProvider = (DBTableProvider) new DBFactory().create(System.getProperty("fizteh.db.dir"));
    }

    @Test
    public void createTableTest() {
        Assert.assertNotNull(tableProvider.createTable("table1"));
        Assert.assertNotNull(tableProvider.createTable("русскоеНазвание"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullcreateTest() {
        tableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyNameCreateTest() {
        tableProvider.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptySpaceTest() {
        tableProvider.createTable("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadNameTableTest() {
        tableProvider.createTable("୧༼ಠ益ಠ༽୨ NOW WE RIOT ୧༼ಠ益ಠ༽୨");
    }


}
