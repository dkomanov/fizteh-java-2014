package ru.fizteh.fivt.students.Bulat_Galiev.junit.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.TabledbProvider;

public class TabledbProviderTest {
    TableProvider provider;
    Path testDir;
    Path testDirTwo;

    @Before
    public void setUp() throws Exception {
        String tmp_dir_prefix = "Swing_";
        String tmp_dir_prefix_two = "Swing_Two";
        testDir = Files.createTempDirectory(tmp_dir_prefix);
        testDirTwo = Files.createTempDirectory(tmp_dir_prefix_two);
        provider = new TabledbProvider(testDir.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPassDirectoryWithNotExistParentToCreate() throws Exception {
        new TabledbProvider(testDirTwo.toString() + File.separatorChar
                + "IDoNot_exist" + File.separatorChar + "IDoNot_existToo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHomeDirectoryContainsFiles() throws Exception {
        testDirTwo.resolve("fileName").toFile().createNewFile();
        new TabledbProvider(testDirTwo.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHomeDirectoryIsFile() throws Exception {
        testDirTwo = testDirTwo.resolve("fileName");
        testDirTwo.toFile().createNewFile();
        new TabledbProvider(testDirTwo.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPassInvalidCharactersDir() throws Exception {
        provider.createTable("/*?*/");
    }

    @Test
    public void testPassNormalDirectory() throws Exception {
        provider = new TabledbProvider(testDirTwo.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNamelessTable() throws Exception {
        provider.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSpacesNameTable() throws Exception {
        provider.createTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidCharactersTable() throws Exception {
        provider.createTable("/*?*/");
    }

    @Test
    public void testCreateNormalTable() throws Exception {
        Assert.assertNotNull(provider.createTable("table"));
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        provider.createTable("table1");
        Assert.assertNull(provider.createTable("table1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNamelessTable() throws Exception {
        provider.getTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInvalidCharactersTable() throws Exception {
        provider.getTable("/*?*/");
    }

    @Test
    public void testGetNotExistingTable() throws Exception {
        Assert.assertNull(provider.getTable("notExistingTable"));
    }

    @Test
    public void testGetExistingTable() throws Exception {
        Table table1 = provider.createTable("firstTable");
        Assert.assertEquals(table1, provider.getTable("firstTable"));
        Table table2 = provider.createTable("secondTable");
        Assert.assertEquals(table1, provider.getTable("firstTable"));
        Assert.assertEquals(table2, provider.getTable("secondTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNamelessTable() throws Exception {
        provider.removeTable("          ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveInvalidCharactersTable() throws Exception {
        provider.removeTable("/*?*/");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }

    @Test
    public void testRemoveExistingTable() throws Exception {
        provider.createTable("table");
        provider.removeTable("table");
        provider.createTable("table1");
        provider.createTable("table2");
        provider.removeTable("table1");
    }

    @After
    public void tearDown() throws Exception {
        Cleaner.clean(testDir.toFile());
    }

}
