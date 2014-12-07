package ru.fizteh.fivt.students.SukhanovZhenya.Junit.Tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.SukhanovZhenya.Junit.Provider;

public class ProviderTest {

    private static Provider testing;

    @Before
    public void setUp() throws Exception {
        testing = new Provider(System.getProperty("fizteh.db.dir"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        testing.getTable(null);
    }
    @Test
    public void testGetTableNotNull() {
        Assert.assertNotNull(testing.getTable("cat"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        testing.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNullArgument() throws Exception {
        testing.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableStateException() throws Exception {
        testing.removeTable("notExistTable");
    }

    @Test
    public void testCreateTableNotNull() throws Exception {
        Assert.assertNotNull(testing.createTable("newTestingTable"));
        testing.removeTable("newTestingTable");
    }
}
