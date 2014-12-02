package ru.fizteh.fivt.students.LevkovMiron.JUnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.Table;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProvider;
import ru.fizteh.fivt.students.LevkovMiron.JUnit.TableProviderFactoryClass;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Мирон on 27.10.2014 PACKAGE_NAME.
 */
public class JUnitTest {

    TableProvider provider;
    File folder;
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        folder = tmpFolder.newFolder();
        provider = new TableProviderFactoryClass().create(folder.getAbsolutePath());
    }

    @Test
    public void testTableProviderFactory() throws IOException {
        File folder = tmpFolder.newFolder();
        Assert.assertNotNull(new TableProviderFactoryClass().create(folder.getAbsolutePath()));
        folder.delete();
        Assert.assertNull(new TableProviderFactoryClass().create(folder.getAbsolutePath()));
        Assert.assertNull(new TableProviderFactoryClass().create(null));
    }



    @Test
    public void testTableProviderCreate() throws IOException {
        provider.createTable("table1");
        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNotNull(provider.createTable("table2"));
        provider.removeTable("table1");
        provider.removeTable("table2");
    }
    @Test
    public void testTableProviderGet() {
        Assert.assertNull(provider.getTable("table2"));
        provider.createTable("table1");
        Assert.assertNotNull(provider.getTable("table1"));
        provider.removeTable("table1");
    }
    @Test
    public void testTableProviderRemove() {
        provider.createTable("table1");
        provider.removeTable("table1");
        Assert.assertNull(provider.getTable("table1"));
    }



    @Test
    public void testTableCommit() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        table.commit();
        table = provider.getTable("table1");
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.get("1"), "2");
        provider.removeTable("table1");
    }
    @Test
    public void testTableRollback() {
        provider.createTable("table2");
        Table table = provider.getTable("table2");
        table.put("1", "2");
        Assert.assertEquals(table.size(), 1);
        table.commit();
        table.put("2", "3");
        table.put("2", "4");
        Assert.assertEquals(table.size(), 2);
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.get("1"), "2");
        table.remove("1");
        table.rollback();
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.get("1"), "2");
        provider.removeTable("table2");
    }
    @Test
    public void testTableGetName() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        Assert.assertEquals(table.getName(), folder.getName());
        provider.removeTable("table1");
    }
    @Test
    public void testTableGet() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        Assert.assertEquals(table.get("1"), "2");
        Assert.assertNull(table.get("2"));
        provider.removeTable("table1");
    }
    @Test
    public void testTablePut() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        Assert.assertNull(table.put("1", "2"));
        Assert.assertEquals(table.put("1", "3"), "2");
        provider.removeTable("table1");
    }
    @Test
    public void testTableSize() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        Assert.assertEquals(table.size(), 0);
        table.put("1", "2");
        Assert.assertEquals(table.size(), 1);
        table.put("2", "3");
        Assert.assertEquals(table.size(), 2);
        table.remove("1");
        Assert.assertEquals(table.size(), 1);
        table.put("2", "3");
        Assert.assertEquals(table.size(), 1);
        provider.removeTable("table1");
    }
    @Test
    public void testTableRemove() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        Assert.assertNull(table.remove("a"));
        Assert.assertEquals(table.remove("1"), "2");
        Assert.assertEquals(table.size(), 0);
        provider.removeTable("table1");
    }
    @Test
    public void testTableList() {
        provider.createTable("table1");
        Table table = provider.getTable("table1");
        table.put("1", "2");
        table.put("2", "3");
        table.put("1", "3");
        List<String> list = table.list();
        Collections.sort(list);
        String[] arrayList = new String[2];
        arrayList[0] = list.get(0);
        arrayList[1] = list.get(1);
        Assert.assertEquals(list.size(), 2);
        String[] testList = new String[2];
        testList[0] = "1";
        testList[1] = "2";
        Assert.assertArrayEquals(testList, arrayList);
        provider.removeTable("table1");
    }
}
