package ru.fizteh.fivt.students.SukhanovZhenya.Junit.Tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.SukhanovZhenya.Junit.FileMap;
import ru.fizteh.fivt.students.SukhanovZhenya.Junit.Provider;

public class FileMapTest {

    private static FileMap testing;

    @Before
    public void setUp() {
        Provider provider = new Provider(System.getProperty("fizteh.db.dir"));
        testing = (FileMap) provider.createTable("TestingTable");

    }

    @After
    public void after() {
        testing.remove();
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(testing.getName(), "TestingTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut() throws Exception {
        testing.put(null, "lol");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        testing.get(null);
    }

    @Test
    public void testGet() throws Exception {
        testing.put("1", "2");
        Assert.assertEquals("2", testing.get("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        testing.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        testing.put("1", "2");

        Assert.assertEquals("2", testing.put("1", "3"));
    }

    @Test
    public void testSize() throws Exception {
        int tmp = testing.size();
        testing.put("20", "20");
        testing.put("1", "100");
        Assert.assertEquals(tmp + 2, testing.size());
    }

    @Test
    public void testRollback() throws Exception {
        testing.put("20", "20");
        testing.put("1", "100");
        Assert.assertEquals(2, testing.rollback());
    }
}
