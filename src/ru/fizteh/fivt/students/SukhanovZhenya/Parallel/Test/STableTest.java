package ru.fizteh.fivt.students.SukhanovZhenya.Parallel.Test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.SProvider;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.SProviderFactory;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.SStoreable;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.STable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class STableTest {
    STable sTable = null;
    SProvider sProvider = null;


    @Before
    public void setUp() throws Exception {
        SProviderFactory sProviderFactory = new SProviderFactory();

        sProvider = sProviderFactory.create("testDir");
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        sTable = sProvider.createTable("test", classList);

        SStoreable storeable;
        List<Class<?>> list = new ArrayList<>();
        list.add(Integer.class);

        List<Object> objects = new ArrayList<>();
        objects.add(Integer.valueOf(10));

        storeable = new SStoreable(objects, list);

        sTable.put("1", storeable);
    }

    @After
    public void remove() throws Exception {
        sTable.remove();
        File dir = new File("testDir");
        if (dir.exists()) {
            dir.delete();
        }
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("test", sTable.getName());
    }


    @Test
    public void types() throws Exception {
        STable tmp = (STable) sProvider.getTable("test");
        Assert.assertEquals(sTable.getTypesList(), tmp.getTypesList());
    }

    @Test
    public void testPutGet() throws Exception {

        Assert.assertNull(sTable.get("not exists"));
        Assert.assertEquals(Integer.valueOf(10), sTable.get("1").getIntAt(0));
    }

    @Test
    public void testList() throws Exception {
        Assert.assertEquals(sTable.list().get(0), "1");
        Assert.assertEquals(sTable.list().size(), 1);
    }

    @Test
    public void testCommit() throws Exception {

        Assert.assertEquals(1, sTable.commit());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(sTable.size(), 1);
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(sTable.rollback(), 1);
        Assert.assertEquals(sTable.size(), 0);
    }

    @Test
    public void testGetNumberOfUncommittedChanges() throws Exception {
        Assert.assertEquals(sTable.getNumberOfUncommittedChanges(), 1);
    }

    @Test
    public void testGetColumnsCount() throws Exception {
        Assert.assertEquals(sTable.getColumnsCount(), 1);
    }

    @Test
    public void testGetColumnType() throws Exception {
        Assert.assertEquals(sTable.getColumnType(0), Integer.class);
        File dir = new File("testDir");
        if (dir.exists()) {
            dir.delete();
        }
    }
}
