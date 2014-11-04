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
    Table table;
    Path testDir;

    @Before
    public void setUp() throws Exception {
        String tmp_dir_prefix = "Swing_";
        testDir = Files.createTempDirectory(tmp_dir_prefix);
        table = new Tabledb(testDir, "test");
    }

    @After
    public void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIncorrectKey() throws Exception {
        table.put("     ", "42");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIncorrectValue() throws Exception {
        table.put("java", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectKey() throws Exception {
        table.get("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectKey() throws Exception {
        table.remove("      ");
    }

    @Test
    public void testPutNormal() throws Exception {
        Assert.assertNull(table.put("1", "2"));
    }

    @Test
    public void testPutOverwrite() throws Exception {
        table.put("1", "2");
        Assert.assertEquals("2", table.put("1", "3"));
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(table.remove("NotExistisngKey"));
    }

    @Test
    public void testRemoveNormal() throws Exception {
        table.put("1", "2");
        Assert.assertEquals("2", table.remove("1"));
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        Assert.assertNull(table.get("NotExistingKey"));
    }

    @Test
    public void testGetNormal() throws Exception {
        table.put("1", "2");
        Assert.assertEquals("2", table.get("1"));
    }

    @Test
    public void testRussian() throws Exception {
        table.put("Ключ", "Значение");
        Assert.assertEquals("Значение", table.get("Ключ"));
    }

    @Test
    public void testGetOverwritten() throws Exception {
        table.put("1", "2");
        table.put("1", "3");
        Assert.assertEquals("3", table.get("1"));
    }

    @Test
    public void testGetRemoved() throws Exception {
        table.put("1", "2");
        table.put("3", "d");
        Assert.assertEquals("d", table.get("3"));
        table.remove("3");
        Assert.assertNull(table.get("3"));
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(0, table.rollback());
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        table.put("useless", "void");
        table.rollback();
        Assert.assertNull(table.get("useless"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        table.put("1", "2");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals("2", table.get("1"));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        table.put("key", "value");
        table.commit();
        table.remove("key");
        table.rollback();
        Assert.assertEquals("value", table.get("key"));
    }

    @Test
    public void testPutRemoveSize() throws Exception {
        table.put("1", "2");
        table.put("2", "3");
        table.remove("3");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        Assert.assertEquals(1, table.size());
    }

    @Test
    public void testPutCommitRollbackSize() throws Exception {
        table.put("1", "2");
        table.put("2", "3");
        table.put("2", "3");
        Assert.assertEquals(2, table.commit());
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("1");
        Assert.assertEquals(0, table.size());
        Assert.assertEquals(2, table.rollback());
        Assert.assertEquals(2, table.size());
    }

}
