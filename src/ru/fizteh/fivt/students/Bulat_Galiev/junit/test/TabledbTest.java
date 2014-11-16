package ru.fizteh.fivt.students.Bulat_Galiev.junit.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.Tabledb;

public class TabledbTest {
    private static final int CHECKNUMBERZERO = 0;
    private static final int CHECKNUMBERONE = 1;
    private static final int CHECKNUMBERTWO = 2;
    private static final int CHECKNUMBERTHREE = 3;
    private Table table;
    private Path testDir;

    @Before
    public final void setUp() throws Exception {
        String tmpDirPrefix = "Swing_";
        testDir = Files.createTempDirectory(tmpDirPrefix);
        table = new Tabledb(testDir, "test");
    }

    @After
    public final void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutNullKey() throws Exception {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutNullValue() throws Exception {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutIncorrectKey() throws Exception {
        table.put("     ", "42");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testPutIncorrectValue() throws Exception {
        table.put("java", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetIncorrectKey() throws Exception {
        table.get("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRemoveIncorrectKey() throws Exception {
        table.remove("      ");
    }

    @Test
    public final void testPutNormal() throws Exception {
        Assert.assertNull(table.put("1", "2"));
    }

    @Test
    public final void testPutOverwrite() throws Exception {
        table.put("1", "2");
        Assert.assertEquals("2", table.put("1", "3"));
    }

    @Test
    public final void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(table.remove("NotExistisngKey"));
    }

    @Test
    public final void testRemoveNormal() throws Exception {
        table.put("1", "2");
        Assert.assertEquals("2", table.remove("1"));
    }

    @Test
    public final void testGetNotExistingKey() throws Exception {
        Assert.assertNull(table.get("NotExistingKey"));
    }

    @Test
    public final void testGetNormal() throws Exception {
        table.put("1", "2");
        Assert.assertEquals("2", table.get("1"));
    }

    @Test
    public final void testRussian() throws Exception {
        table.put("Ключ", "Значение");
        Assert.assertEquals("Значение", table.get("Ключ"));
    }

    @Test
    public final void testGetOverwritten() throws Exception {
        table.put("1", "2");
        table.put("1", "3");
        Assert.assertEquals("3", table.get("1"));
    }

    @Test
    public final void testGetRemoved() throws Exception {
        table.put("1", "2");
        table.put("3", "d");
        Assert.assertEquals("d", table.get("3"));
        table.remove("3");
        Assert.assertNull(table.get("3"));
    }

    @Test
    public final void testCommit() throws Exception {
        Assert.assertEquals(CHECKNUMBERZERO, table.commit());
    }

    @Test
    public final void testRollback() throws Exception {
        Assert.assertEquals(CHECKNUMBERZERO, table.rollback());
    }

    @Test
    public final void testSize() throws Exception {
        Assert.assertEquals(CHECKNUMBERZERO, table.size());
    }

    @Test
    public final void testPutRollbackGet() throws Exception {
        table.put("useless", "void");
        table.rollback();
        Assert.assertNull(table.get("useless"));
    }

    @Test
    public final void testPutCommitGet() throws Exception {
        table.put("1", "2");
        Assert.assertEquals(CHECKNUMBERONE, table.commit());
        Assert.assertEquals("2", table.get("1"));
    }

    @Test
    public final void testGetdiffnrecords() throws Exception {
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        Assert.assertEquals(CHECKNUMBERTHREE,
                ((Tabledb) table).getdiffnrecords());
    }

    @Test
    public final void testPutCommitRemoveRollbackGet() throws Exception {
        table.put("key", "value");
        table.commit();
        table.remove("key");
        table.rollback();
        Assert.assertEquals("value", table.get("key"));
    }

    @Test
    public final void testPutRemoveSize() throws Exception {
        table.put("1", "2");
        table.put("2", "3");
        table.remove("3");
        Assert.assertEquals(CHECKNUMBERTWO, table.size());
        table.remove("2");
        Assert.assertEquals(CHECKNUMBERONE, table.size());
    }

    @Test
    public final void testDeleteTable() throws Exception {
        ((Tabledb) table).deleteTable();
        Assert.assertEquals(CHECKNUMBERZERO, table.size());
    }

    @Test
    public final void testPutCommitRollbackSize() throws Exception {
        table.put("1", "2");
        table.put("2", "3");
        table.put("2", "3");
        Assert.assertEquals(CHECKNUMBERTWO, table.commit());
        Assert.assertEquals(CHECKNUMBERTWO, table.size());
        table.remove("2");
        table.remove("1");
        Assert.assertEquals(CHECKNUMBERZERO, table.size());
        Assert.assertEquals(CHECKNUMBERTWO, table.rollback());
        Assert.assertEquals(CHECKNUMBERTWO, table.size());
    }

}
